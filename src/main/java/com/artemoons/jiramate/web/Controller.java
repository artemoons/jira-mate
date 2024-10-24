package com.artemoons.jiramate.web;

import com.artemoons.jiramate.service.JiraQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for interaction with bot functions (currently for debug purposes).
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@RestController
@RequestMapping(value = "/rest/api/v1")
public class Controller {

    /**
     * Jira query service.
     */
    private final JiraQueryService queryService;

    /**
     * Constructor.
     *
     * @param service Jira query service
     */
    @Autowired
    public Controller(final JiraQueryService service) {
        this.queryService = service;
    }

    /**
     * Debug controller.
     */
    @GetMapping("/query")
    private void getJiraResults() {
        queryService.getDailyReport();
    }

}
