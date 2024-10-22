package com.artemoons.jiramate.client;

import com.artemoons.jiramate.dto.JiraResponse;
import com.artemoons.jiramate.dto.WorktimeResponse;
import org.json.JSONObject;

/**
 * Интерфейс для работы с клиентом Jira.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
public interface JiraHttpClient {

    /**
     * Метод для получения всех записей о работе.
     *
     * @param payload сформированное тело запроса
     * @return ответ
     */
    JiraResponse[] getWorklogs(JSONObject payload);

    /**
     * Метод для получения доступного времени работы за период.
     *
     * @param payload сформированное тело запроса
     * @return ответ, содержащий информацию о том, сколько времени было доступно для трекинга
     */
    WorktimeResponse getRequiredTimeForPeriod(JSONObject payload);

}
