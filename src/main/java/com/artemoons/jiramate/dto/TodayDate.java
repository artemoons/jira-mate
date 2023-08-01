package com.artemoons.jiramate.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Builder
@Getter
public class TodayDate {

    private final int day;

    private final int month;

    private final int year;

    private final LocalDate dayObject;

}
