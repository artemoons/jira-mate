package com.artemoons.jiramate.client;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;
import org.json.JSONObject;

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
    JiraResponse[] getWorklogs(JSONObject payload);

    /**
     * Method for obtaining available worklog time for period.
     *
     * @param payload request body
     * @return available worklog time
     */
    WorktimeResponse getRequiredTimeForPeriod(JSONObject payload);

}
