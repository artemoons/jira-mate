package com.artemoons.jiramate.service.impl;

import com.artemoons.jiramate.service.ActionWrapper;
import com.artemoons.jiramate.service.JiraQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Service
public class ActionWrapperImpl implements ActionWrapper {

    @Autowired
    JiraQueryService service;

    @Override
    public void processUpdate(Update update) {

        if (update.getMessage().getText().equals("/daily-report")) {
            service.getDailyReport();
        }
        if (update.getMessage().getText().equals("/weekly-report")) {
            service.getWeeklyReport();
        }

    }
}
