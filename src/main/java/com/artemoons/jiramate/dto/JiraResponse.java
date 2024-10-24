package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Jira response DTO.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JiraResponse {

    /**
     * Time spent in seconds.
     */
    @JsonProperty("timeSpentSeconds")
    private Double timeSpentSeconds;

    /**
     * Worklog date.
     */
    @JsonProperty("started")
    private String worklogDate;

    /**
     * Worklog author.
     */
    @JsonProperty("worker")
    private String worklogAuthor;

}
