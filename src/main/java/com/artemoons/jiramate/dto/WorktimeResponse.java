package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Available worklog information DTO.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorktimeResponse {

    /**
     * Number of work days.
     */
    @JsonProperty("numberOfWorkingDays")
    private Integer workingDays;

    /**
     * Available log time in seconds.
     */
    @JsonProperty("requiredSeconds")
    private Long workingTime;

}
