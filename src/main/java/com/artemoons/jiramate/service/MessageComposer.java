package com.artemoons.jiramate.service;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.TodayDate;
import com.artemoons.jiramate.dto.WorktimeResponse;

/**
 * Message composer interface.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface MessageComposer {

    /**
     * Method for preparing daily message.
     *
     * @param worklogHours     worklog information
     * @param worktimeResponse available time information
     * @param today            current day
     * @return generated message
     */
    String prepareDailyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse, TodayDate today);

    /**
     * Method for preparing weekly message.
     *
     * @param worklogHours     worklog information
     * @param worktimeResponse available time information
     * @param today            current day
     * @return generated message
     */
    String prepareWeeklyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse, TodayDate today);

    /**
     * Method for preparing monthly message
     *
     * @param worklogHours     worklog information
     * @param worktimeResponse available time information
     * @param today            current day
     * @return generated message
     */
    String prepareMonthlyMessage(JiraResponse[] worklogHours, WorktimeResponse worktimeResponse, TodayDate today);

}
