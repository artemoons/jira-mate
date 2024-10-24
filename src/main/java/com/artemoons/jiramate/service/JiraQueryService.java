package com.artemoons.jiramate.service;

/**
 * Jira query service interface.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface JiraQueryService {

    /**
     * Method for obtaining daily report.
     */
    void getDailyReport();

    /**
     * Method for obtaining weekly report.
     */
    void getWeeklyReport();

    /**
     * Method for obtaining monthly report.
     */
    void getMonthlyReport();

}
