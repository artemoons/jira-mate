package com.artemoons.jiramate.web;

import com.artemoons.jiramate.service.JiraQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@RestController
@RequestMapping(value = "/rest/api/v1")
public class Controller {

    JiraQueryService service;

    @Autowired
    public Controller(JiraQueryService service) {
        this.service = service;
    }


    @GetMapping("/query")
    private void getJiraResults() {


        service.getDailyReport();

    }


}
