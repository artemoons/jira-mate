package com.artemoons.jiramate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Ответ с информацией о доступном времени работы за период.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorktimeResponse {

    /**
     * Число рабочих дней.
     */
    @JsonProperty("numberOfWorkingDays")
    private Integer workingDays;

    /**
     * Количество доступного времени за период (в мс).
     */
    @JsonProperty("requiredSeconds")
    private Long workingTime;

}
