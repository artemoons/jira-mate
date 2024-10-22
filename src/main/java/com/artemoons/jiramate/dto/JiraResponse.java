package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Ответ Jira.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JiraResponse {

    /**
     * Затраченное время (в мс).
     */
    @JsonProperty("timeSpentSeconds")
    private Double timeSpentSeconds;

    /**
     * Дата записи о работе.
     */
    @JsonProperty("started")
    private String worklogDate;

    /**
     * Имя автора записи о работе.
     */
    @JsonProperty("worker")
    private String worklogAuthor;

}
