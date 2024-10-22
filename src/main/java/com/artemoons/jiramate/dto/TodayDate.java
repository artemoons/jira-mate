package com.artemoons.jiramate.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Вспомогательное DTO для текущей даты.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Builder
@Getter
public class TodayDate {

    /**
     * День.
     */
    private final int day;

    /**
     * Месяц.
     */
    private final int month;

    /**
     * Год.
     */
    private final int year;

    /**
     * Объект текущего дня.
     */
    private final LocalDate dayObject;

}
