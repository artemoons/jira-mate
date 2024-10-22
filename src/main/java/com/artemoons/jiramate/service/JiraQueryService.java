package com.artemoons.jiramate.service;

/**
 * Интерфейс для работы с Jira.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface JiraQueryService {

    /**
     * Метод для получения отчёта за день.
     */
    void getDailyReport();

    /**
     * Метод для получения отчёта за неделю.
     */
    void getWeeklyReport();

    /**
     * Метод для получения отчёта за месяц.
     */
    void getMonthlyReport();

}
