package com.artemoons.jiramate.service;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;

/**
 * Интерфейс для работы с составителем сообщения.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface MessageComposer {

    /**
     * Метод для подготовки сообщения о записях о работе за день.
     *
     * @param worklogHours     информация о затраченных часах
     * @param worktimeResponse информация о доступных часах
     * @return сформированное сообщения
     */
    String prepareDailyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse);

    /**
     * Метод для подготовки сообщения о записях о работе за неделю.
     *
     * @param worklogHours     информация о затраченных часах
     * @param worktimeResponse информация о доступных часах
     * @return сформированное сообщения
     */
    String prepareWeeklyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse);

    /**
     * Метод для подготовки сообщения о записях о работе за месяц.
     *
     * @param worklogHours     информация о затраченных часах
     * @param worktimeResponse информация о доступных часах
     * @return сформированное сообщения
     */
    String prepareMonthlyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse);

}
