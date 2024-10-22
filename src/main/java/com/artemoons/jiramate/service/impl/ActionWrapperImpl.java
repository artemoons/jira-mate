package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.service.ActionWrapper;
import com.artemoons.jiramate.service.JiraQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Вспомогательная обёртка над вызовом методов.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Service
public class ActionWrapperImpl implements ActionWrapper {

    /**
     * Сервис для работы с Jira.
     */
    @Autowired
    private JiraQueryService service;

    /**
     * Метод обработки полученного сообщения.
     *
     * @param update сообщение
     */
    @Override
    public void processUpdate(final Update update) {

        if (update.getMessage().getText().equals("/daily_report")) {
            service.getDailyReport();
        }
        if (update.getMessage().getText().equals("/weekly_report")) {
            service.getWeeklyReport();
        }
        if (update.getMessage().getText().equals("/monthly_report")) {
            service.getMonthlyReport();
        }
    }
}