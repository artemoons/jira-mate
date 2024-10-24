package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * DTO for requesting Jira worklogs.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Getter
@Builder
public class JiraQuery {

    /**
     * Start period date.
     */
    @JsonProperty("from")
    private final String fromDate;

    /**
     * End period date.
     */
    @JsonProperty("to")
    private final String toDate;

    /**
     * List of Jira usernames.
     */
    @JsonProperty("worker")
    private final List<String> worker;

}
