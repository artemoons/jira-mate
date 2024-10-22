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

import java.time.DayOfWeek;
import java.time.LocalDate;
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
     * Стартовое число месяца.
     */
    public static final int START_DAY_OF_MONTH = 1;
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
        TodayDate today = getCurrentDate();
        JSONObject dayPayload = preparePayload(today, false);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(dayPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(dayPayload);

        String textMessage = messageComposer.prepareDailyMessage(usersLogTime, requiredLogTime);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF DAILY REPORT == ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getWeeklyReport() {
        TodayDate today = getCurrentDate();
        JSONObject weekPayload = preparePayload(today, true);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(weekPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(weekPayload);

        String textMessage = messageComposer.prepareWeeklyMessage(usersLogTime, requiredLogTime);
        messageSender.sendMessage(textMessage);
        log.info(" == END OF WEEKLY REPORT == ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getMonthlyReport() {
        TodayDate today = getCurrentDate();
        JSONObject monthPayload = preparePayload(today);

        JiraResponse[] usersLogTime = jiraHttpClient.getWorklogs(monthPayload);
        WorktimeResponse requiredLogTime = jiraHttpClient.getRequiredTimeForPeriod(monthPayload);

        String textMessage = messageComposer.prepareMonthlyMessage(usersLogTime, requiredLogTime);
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
        LocalDate localDate = today.getDayObject();

        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("from", year + "-" + month + "-" + START_DAY_OF_MONTH);
        jsonObject.put("to", year + "-" + month + "-" + day);
        jsonObject.put("worker", getUserNames());
        log.info(" == MONTHLY REPORT == ");
        log.info("FROM {}-{}-{} TO {}-{}-{}", START_DAY_OF_MONTH, month, year, day, month, year);
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
        int year = today.getYear();
        int month = today.getMonth();
        int day = today.getDay();
        JSONObject jsonObject = new JSONObject();
        if (fromWeekStart) {
            LocalDate monday = today.getDayObject();
            while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
                monday = monday.minusDays(1);
            }

            int mondayDay = monday.getDayOfMonth();
            int mondayMonth = monday.getMonthValue();
            int mondayYear = monday.getYear();

            jsonObject.put("from", mondayYear + "-" + mondayMonth + "-" + mondayDay);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", getUserNames());
            log.info(" == WEEKLY REPORT == ");
            log.info("FROM {}-{}-{} TO {}-{}-{}", mondayDay, mondayMonth, mondayYear, day, month, year);
        } else {
            jsonObject.put("from", year + "-" + month + "-" + day);
            jsonObject.put("to", year + "-" + month + "-" + day);
            jsonObject.put("worker", getUserNames());
            log.info(" == DAILY REPORT == ");
            log.info("DATE: {}-{}-{}", day, month, year);
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

    /**
     * Вспомогательный метод для полуяения текущей даты.
     *
     * @return текущая дата
     */
    public static TodayDate getCurrentDate() {
        LocalDate today = LocalDate.now();
        return TodayDate.builder()
                .day(today.getDayOfMonth())
                .month(today.getMonthValue())
                .year(today.getYear())
                .dayObject(today)
                .build();
    }
}
