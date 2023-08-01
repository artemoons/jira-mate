package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JiraResponse {

    @JsonProperty("timeSpentSeconds")
    private Double timeSpentSeconds;

    @JsonProperty("started")
    private String worklogDate;

    @JsonProperty("worker")
    private String worklogAuthor;

}
