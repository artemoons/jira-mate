package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.client.JiraHttpClient;
import com.artemoons.jiramate.client.impl.JiraHttpClientImpl;
import com.artemoons.jiramate.config.BotConfiguration;
import com.artemoons.jiramate.dto.JiraQuery;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.service.JiraQueryService;
import com.artemoons.jiramate.service.MessageComposer;
import com.artemoons.jiramate.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Jira query service.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class JiraQueryServiceImpl implements JiraQueryService {

    /**
     * Users list.
     */
    @Value("${integration.jira.user-list}")
    private List<String> userList;
    /**
     * Telegram message sender service.
     */
    private final MessageSender messageSender;
    /**
     * Message composer service.
     */
    private final MessageComposer messageComposer;
    /**
     * Jira HTTP client.
     */
    private final JiraHttpClient jiraHttpClient;

    /**
     * Constructor.
     *
     * @param composer         message composer service
     * @param botConfiguration bot configuration
     * @param jiraClient       Jira http client
     */
    @Autowired
    public JiraQueryServiceImpl(final MessageComposer composer,
                                final BotConfiguration botConfiguration,
                                final JiraHttpClientImpl jiraClient) {
        this.messageComposer = composer;
        this.jiraHttpClient = jiraClient;
        this.messageSender = new MessageSender(botConfiguration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getDailyReport() {
        TodayDate today = TodayDate.getCurrentDay();
        JiraQuery dayPayload = preparePayload(today, false);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(dayPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(dayPayload);

        String textMessage = messageComposer.prepareDailyMessage(usersLogTime, requiredLogTime, today);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF DAILY REPORT == ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getWeeklyReport() {
        TodayDate today = TodayDate.getCurrentDay();
        JiraQuery weekPayload = preparePayload(today, true);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(weekPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(weekPayload);

        String textMessage = messageComposer.prepareWeeklyMessage(usersLogTime, requiredLogTime, today);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF WEEKLY REPORT == ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getMonthlyReport() {
        TodayDate today = TodayDate.getCurrentDay();
        JiraQuery monthPayload = preparePayload(today);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(monthPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(monthPayload);

        String textMessage = messageComposer.prepareMonthlyMessage(usersLogTime, requiredLogTime, today);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF MONTHLY REPORT == ");
    }

    /**
     * Auxiliary method for constructing message body for Jira request (per month).
     *
     * @param today current date
     * @return response
     */
    private JiraQuery preparePayload(final TodayDate today) {
        JiraQuery jiraQuery = JiraQuery.builder()
                .fromDate(today.getYear() + "-" + today.getMonth() + "-" + today.getStartDay())
                .toDate(today.getYear() + "-" + today.getMonth() + "-" + today.getEndDay())
                .worker(getUserNames())
                .build();
        log.info(" == MONTHLY REPORT == ");
        log.info("FROM {}-{}-{} TO {}-{}-{}",
                today.getStartDay(),
                today.getMonth(),
                today.getYear(),
                today.getEndDay(),
                today.getMonth(),
                today.getYear());
        return jiraQuery;
    }

    /**
     * Auxiliary method for constructing message body for Jira request (per day / week).
     *
     * @param today         current date
     * @param fromWeekStart week start flag: true - start from Monday, false - start from current date
     * @return response
     */
    private JiraQuery preparePayload(final TodayDate today, final boolean fromWeekStart) {
        JiraQuery jiraQuery;
        if (fromWeekStart) {
            jiraQuery = JiraQuery.builder()
                    .fromDate(today.getMondayYear() + "-" + today.getMondayMonth() + "-" + today.getMondayDay())
                    .toDate(today.getSundayYear() + "-" + today.getSundayMonth() + "-" + today.getSundayDay())
                    .worker(getUserNames())
                    .build();
            log.info(" == WEEKLY REPORT == ");
            log.info("FROM {}-{}-{} TO {}-{}-{}",
                    today.getMondayDay(),
                    today.getMondayMonth(),
                    today.getMondayYear(),
                    today.getSundayDay(),
                    today.getSundayMonth(),
                    today.getSundayYear());
        } else {
            String requestString = today.getYear() + "-" + today.getMonth() + "-" + today.getDay();
            jiraQuery = JiraQuery.builder()
                    .fromDate(requestString)
                    .toDate(requestString)
                    .worker(getUserNames())
                    .build();
            log.info(" == DAILY REPORT == ");
            log.info("DATE: {}-{}-{}", today.getDay(), today.getMonth(), today.getYear());
        }
        return jiraQuery;
    }

    /**
     * Auxiliary method for obtaining user list.
     *
     * @return users login list
     */
    private List<String> getUserNames() {
        // ^[^/]* before
        // .?[^/]*$ after
        List<String> users = new ArrayList<>(userList);
        int i = 0;
        String newValue;
        for (String item : users) {
            if (item.contains("/")) {
                newValue = item.replaceAll(".?[^/]*$", "");
                users.set(i, newValue);
            }
            i++;
        }
        return users;
    }
}
