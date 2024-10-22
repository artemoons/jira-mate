package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.dto.WorktimeResponse;
import com.artemoons.jiramate.service.MessageComposer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.artemoons.jiramate.service.impl.JiraQueryServiceImpl.getCurrentDate;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Slf4j
@Component
public class MessageComposerImpl implements MessageComposer {

    /**
     * Коэффициент для перевода секунд в часы.
     */
    public static final Double TO_HOURS = 3_600.0;

    /**
     * Список пользователей.
     */
    @Value("${integration.jira.user-list}")
    private List<String> userList;

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareDailyMessage(final JiraResponse[] worklogHours, final WorktimeResponse worktimeResponse) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        LocalDate today = LocalDate.now();
        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        return "🤖 Сводка по записям о работе *за " + today.getDayOfMonth() + "\\/" + today.getMonthValue() + "\\/"
                + today.getYear() + " \\(" + workHours + " ч\\.\\)*:\n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareWeeklyMessage(final JiraResponse[] worklogHours, final WorktimeResponse worktimeResponse) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        return "🤖 Сводка по записям о работе *за неделю*\\.\n\n"
                + "🕗 Рабочих дней: " + worktimeResponse.getWorkingDays() + ", часов: " + workHours + "\\. \n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prepareMonthlyMessage(final JiraResponse[] worklogHours, final WorktimeResponse worktimeResponse) {

        Map<String, Double> worklogData = initWorklogMap();
        populateWorklog(worklogData, worklogHours);

        int workHours = new BigDecimal(worktimeResponse.getWorkingTime() / TO_HOURS).intValue();

        final String[] userWorklogInfo = {""};
        prepareMessageLines(worklogData, workHours, userWorklogInfo);

        String periodString = getPeriodString();
        return "🤖 Сводка по записям о работе \n *за месяц " + periodString + "*:\n\n"
                + "🕗 Рабочих дней: " + worktimeResponse.getWorkingDays() + ", часов: " + workHours + "\\. \n\n"
                + userWorklogInfo[0]
                + "\n↪ [Перейти в Jira](https://jira.cbr.ru/secure/Dashboard.jspa)";

    }

    /**
     * Вспомогательный метод для обогащения записей информацией о затраченном времени.
     *
     * @param worklogData  исходные данные
     * @param worklogHours часы работы по пользователям
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
     * Вспомогательный метод для обновления имён.
     *
     * @param worklogData обновлённая информация - имена в человекочитаемом виде
     */
    private void updateNames(final Map<String, Double> worklogData) {
        Map<String, Double> tempWorklogData = new HashMap<>();
        List<String> replacementMap = userList.stream().filter(name -> name.contains("/")).collect(Collectors.toList());
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
     * Вспомогательный метод для инициализации карты заспией о работе.
     *
     * @return проинициализированный объект
     */
    private Map<String, Double> initWorklogMap() {
        Map<String, Double> worklogData = new Hashtable<>();
        getUserNames().forEach(userName -> worklogData.put(userName, 0.0));
        return worklogData;
    }

    /**
     * Метод для получения имён пользователей, пригодных к отправке в Jira.
     *
     * @return имена пользователей
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
     * Вспомогательный метод для подготовки строки о работе для каждого из пользователей.
     *
     * @param worklogData     информация о работе
     * @param workingHours    информация о доступных часах работы
     * @param userWorklogInfo информация о работе пользователя
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

    /**
     * Вспомогательный метод для формирования строки.
     *
     * @return строка
     */
    private static String getPeriodString() {
        TodayDate currentDate = getCurrentDate();
        int day = currentDate.getDay();
        int month = currentDate.getMonth();
        int year = currentDate.getYear();
        return "\\(c 1\\-" + month + "\\-" + year + " по " + day + "\\-" + month + "\\-" + year + "\\)";
    }
}
