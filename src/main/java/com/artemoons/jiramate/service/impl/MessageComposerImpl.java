package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.service.MessageComposer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Message composing service.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public class MessageComposerImpl implements MessageComposer {

    /**
     * Coefficient for converting seconds to hours.
     */
    public static final Double TO_HOURS = 3_600.0;

    /**
     * User list.
     */
    @Value("${integration.jira.user-list}")
    private List<String> userList;

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareDailyMessage(final JiraResponse[] worklogHours,
                                      final WorktimeResponse worktimeResponse,
                                      final TodayDate today) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        return "🤖 Сводка по записям о работе *за день*\\.\n\n"
                + "📅 " + today.getDay() + "\\." + today.getMonth() + "\\." + today.getYear() + "\n\n"
                + "🕗 Рабочих дней: " + worktimeResponse.getWorkingDays() + ", часов: " + workHours + "\\. \n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareWeeklyMessage(final JiraResponse[] worklogHours,
                                       final WorktimeResponse worktimeResponse,
                                       final TodayDate today) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        return "🤖 Сводка по записям о работе *за неделю*\\.\n\n"
                + "📅 " + today.getMondayDay() + "\\." + today.getMondayMonth() + "\\." + today.getMondayYear() + " \\- "
                + today.getSundayDay() + "\\." + today.getSundayMonth() + "\\." + today.getSundayYear() + "\n\n"
                + "🕗 Рабочих дней: " + worktimeResponse.getWorkingDays() + ", часов: " + workHours + "\\. \n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareMonthlyMessage(final JiraResponse[] worklogHours,
                                        final WorktimeResponse worktimeResponse,
                                        final TodayDate today) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        return "🤖 Сводка по записям о работе \n *за месяц*:\n\n"
                + "📅 " + today.getStartDay() + "\\." + today.getStartMonth() + "\\." + today.getStartYear() + " \\- "
                + today.getEndDay() + "\\." + today.getEndMonth() + "\\." + today.getEndYear() + "\n\n"
                + "🕗 Рабочих дней: " + worktimeResponse.getWorkingDays() + ", часов: " + workHours + "\\. \n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";

    }

    /**
     * Auxiliary method for populating records with time spent information.
     *
     * @param worklogData  source data
     * @param worklogHours user worklogs
     */
    private void populateWorklog(final Map<String, Double> worklogData, final JiraResponse[] worklogHours) {
        for (JiraResponse item : worklogHours) {
            String worklogAuthor = item.getWorklogAuthor();
            if (worklogData.containsKey(worklogAuthor)) {
                Double previousWorklog = worklogData.get(worklogAuthor);
                worklogData.put(worklogAuthor, previousWorklog + item.getTimeSpentSeconds());
            } else {
                worklogData.put(worklogAuthor, item.getTimeSpentSeconds());
            }
        }
        updateNames(worklogData);
    }

    /**
     * Auxiliary method for updating names.
     *
     * @param worklogData usernames in human-readable view
     */
    private void updateNames(final Map<String, Double> worklogData) {
        Map<String, Double> tempWorklogData = new HashMap<>();
        List<String> replacementMap = userList.stream().filter(name -> name.contains("/")).toList();
        Map<String, String> replacementDictionary = new HashMap<>();
        replacementMap.forEach(item -> {
            replacementDictionary.put(item.replaceAll(".?[^/]*$", ""),
                    item.replaceAll("^[^/]*.", ""));
        });

        worklogData.forEach((k, v) -> {
            tempWorklogData.put(replacementDictionary.getOrDefault(k, k), v);
        });

        worklogData.clear();
        worklogData.putAll(tempWorklogData);
    }

    /**
     * Auxiliary method for initialization worklog map.
     *
     * @return initialized object
     */
    private Map<String, Double> initWorklogMap() {
        Map<String, Double> worklogData = new Hashtable<>();
        getUserNames().forEach(userName -> worklogData.put(userName, 0.0));
        return worklogData;
    }

    /**
     * Method for obtaining usernames, suitable for Jira API.
     *
     * @return user names
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

    /**
     * Auxiliary method for preparing user worklog string.
     *
     * @param worklogData     worklog information
     * @param workingHours    available worklog hours
     * @param userWorklogInfo users' worklog information
     */
    private static void prepareMessageLines(final Map<String, Double> worklogData,
                                            final int workingHours,
                                            final String[] userWorklogInfo) {
        worklogData.forEach((key, value) -> {
            Double timeInHours = value / TO_HOURS;
            log.info("{} - {}", key, timeInHours);

            String emojiIcon = "✅";
            if (value == 0 || timeInHours < workingHours) {
                emojiIcon = "❌";
            }
            userWorklogInfo[0] = userWorklogInfo[0].concat(String.format(emojiIcon + " %s \\- %.2f ч\\. \n",
                    key,
                    timeInHours));
        });
    }
}
