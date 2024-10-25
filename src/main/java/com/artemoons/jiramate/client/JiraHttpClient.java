package com.artemoons.jiramate.client;

import com.artemoons.jiramate.dto.JiraPayload;
import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;

/**
 * Jira client interface.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface JiraHttpClient {

    /**
     * Method for obtaining all worklogs.
     *
     * @param payload request body
     * @return response
     */
    JiraResponse[] getWorklogs(JiraPayload payload);

    /**
     * Method for obtaining available worklog time for period.
     *
     * @param payload request body
     * @return available worklog time
     */
    WorktimeResponse getRequiredTimeForPeriod(JiraPayload payload);

}
