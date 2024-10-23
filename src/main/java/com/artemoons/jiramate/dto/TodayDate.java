package com.artemoons.jiramate.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
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
     * Объект текущего дня.
     */
    private final LocalDate dayObject;
    /**
     * Текущий день.
     */
    private final int day;
    /**
     * Текущий месяц.
     */
    private final int month;
    /**
     * Текущий год.
     */
    private final int year;
    /**
     * День начала недели (ПН).
     */
    private final int mondayDay;
    /**
     * Месяц начала недели.
     */
    private final int mondayMonth;
    /**
     * Год начала недели.
     */
    private final int mondayYear;
    /**
     * День конца недели (ВС).
     */
    private final int sundayDay;
    /**
     * Месяц конца недели.
     */
    private final int sundayMonth;
    /**
     * Год конца недели.
     */
    private final int sundayYear;
    /**
     * День начала месяца.
     */
    private final int startDay;
    /**
     * Месяц дня начала месяца.
     */
    private final int startMonth;
    /**
     * Год дня начала месяца.
     */
    private final int startYear;
    /**
     * Конечный день месяца.
     */
    private final int endDay;
    /**
     * Месяц конечного дня месяца.
     */
    private final int endMonth;
    /**
     * Год конечного дня месяца.
     */
    private final int endYear;

    /**
     * Вспомогательный метод для получения текущего дня и зависимых от него величин.
     *
     * @return объект сегодняшнего дня
     */
    public static TodayDate getCurrentDay() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        int todayYear = today.getYear();
        int todayMonth = today.getMonthValue();
        int todayDay = today.getDayOfMonth();

        int mondayDay = weekStart.getDayOfMonth();
        int mondayMonth = weekStart.getMonthValue();
        int mondayYear = weekStart.getYear();

        int sundayDay = weekEnd.getDayOfMonth();
        int sundayMonth = weekEnd.getMonthValue();
        int sundayYear = weekEnd.getYear();

        int startDay = today.withDayOfMonth(1).getDayOfMonth();
        int startMonth = today.withDayOfMonth(1).getMonthValue();
        int startYear = today.withDayOfMonth(1).getYear();
        int endDay = today.withDayOfMonth(today.lengthOfMonth()).getDayOfMonth();
        int endMonth = today.withDayOfMonth(today.lengthOfMonth()).getMonthValue();
        int endYear = today.withDayOfMonth(today.lengthOfMonth()).getYear();

        return TodayDate.builder()
                .day(todayDay)
                .month(todayMonth)
                .year(todayYear)
                .mondayDay(mondayDay)
                .mondayMonth(mondayMonth)
                .mondayYear(mondayYear)
                .sundayDay(sundayDay)
                .sundayMonth(sundayMonth)
                .sundayYear(sundayYear)
                .startDay(startDay)
                .startMonth(startMonth)
                .startYear(startYear)
                .endDay(endDay)
                .endMonth(endMonth)
                .endYear(endYear)
                .dayObject(today)
                .build();
    }
}
