package com.artemoons.jiramate.web;

import com.artemoons.jiramate.service.JiraQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с ботом.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@RestController
@RequestMapping(value = "/rest/api/v1")
public class Controller {

    /**
     * Сервис работы с Jira.
     */
    private final JiraQueryService queryService;

    /**
     * Конструктор.
     *
     * @param service сервис работы с Jira
     */
    @Autowired
    public Controller(final JiraQueryService service) {
        this.queryService = service;
    }

    /**
     * Отладочный контроллер.
     */
    @GetMapping("/query")
    private void getJiraResults() {
        queryService.getDailyReport();
    }

}
