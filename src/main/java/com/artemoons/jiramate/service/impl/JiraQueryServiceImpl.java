package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.client.JiraHttpClient;
import com.artemoons.jiramate.client.impl.JiraHttpClientImpl;
import com.artemoons.jiramate.config.BotConfiguration;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.service.JiraQueryService;
import com.artemoons.jiramate.service.MessageComposer;
import com.artemoons.jiramate.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис обращения к Jira.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Service
public class JiraQueryServiceImpl implements JiraQueryService {

    /**
     * Список пользователей.
     */
    @Value("${integration.jira.user-list}")
    private List<String> userList;
    /**
     * Сервис отправки сообщения в Telegram.
     */
    private final MessageSender messageSender;
    /**
     * Сервис формирования сообщения.
     */
    private final MessageComposer messageComposer;
    /**
     * Клиент для обращения к Jira.
     */
    private final JiraHttpClient jiraHttpClient;

    /**
     * Конструктор.
     *
     * @param composer         сервис формирования сообщения
     * @param botConfiguration конфигурация бота
     * @param jiraClient       клиент для обращения к jira
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
        JSONObject dayPayload = preparePayload(today, false);

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
        JSONObject weekPayload = preparePayload(today, true);

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
        JSONObject monthPayload = preparePayload(today);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(monthPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(monthPayload);

        String textMessage = messageComposer.prepareMonthlyMessage(usersLogTime, requiredLogTime, today);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF MONTHLY REPORT == ");
    }

    /**
     * Вспомогательный метод для формирования тела запроса к Jira (данные за месяц).
     *
     * @param today текущая дата
     * @return ответ
     */
    private JSONObject preparePayload(final TodayDate today) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("from", today.getYear() + "-" + today.getMonth() + "-" + today.getStartDay());
        jsonObject.put("to", today.getYear() + "-" + today.getMonth() + "-" + today.getEndDay());
        jsonObject.put("worker", getUserNames());
        log.info(" == MONTHLY REPORT == ");
        log.info("FROM {}-{}-{} TO {}-{}-{}",
                today.getStartDay(),
                today.getMonth(),
                today.getYear(),
                today.getEndDay(),
                today.getMonth(),
                today.getYear());
        return jsonObject;
    }

    /**
     * Вспомогательный метод для формирования тела запроса к Jira (данные за день / неделю).
     *
     * @param today         текущая дата
     * @param fromWeekStart флаг начала недели: true - запрос периода с первого дня текущей недели,
     *                      false - запрос за день
     * @return ответ
     */
    private JSONObject preparePayload(final TodayDate today, final boolean fromWeekStart) {
        JSONObject jsonObject = new JSONObject();
        if (fromWeekStart) {
            jsonObject.put("from", today.getMondayYear() + "-" + today.getMondayMonth() + "-" + today.getMondayDay());
            jsonObject.put("to", today.getSundayYear() + "-" + today.getSundayMonth() + "-" + today.getSundayDay());
            jsonObject.put("worker", getUserNames());
            log.info(" == WEEKLY REPORT == ");
            log.info("FROM {}-{}-{} TO {}-{}-{}",
                    today.getMondayDay(),
                    today.getMondayMonth(),
                    today.getMondayYear(),
                    today.getSundayDay(),
                    today.getSundayMonth(),
                    today.getSundayYear());
        } else {
            jsonObject.put("from", today.getYear() + "-" + today.getMonth() + "-" + today.getDay());
            jsonObject.put("to", today.getYear() + "-" + today.getMonth() + "-" + today.getDay());
            jsonObject.put("worker", getUserNames());
            log.info(" == DAILY REPORT == ");
            log.info("DATE: {}-{}-{}", today.getDay(), today.getMonth(), today.getYear());
        }
        return jsonObject;
    }

    /**
     * Вспомогательный метод для получения списка пользователей.
     *
     * @return список логинов пользователей
     */
    private List<String> getUserNames() {
        // ^[^/]* before
        // .?[^/]*$ after
        List<String> users = new ArrayList<>(userList);
        int i = 0;
        String newValue = "null";
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
