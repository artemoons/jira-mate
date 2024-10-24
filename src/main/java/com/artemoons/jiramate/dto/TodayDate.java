package com.artemoons.jiramate.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Auxiliary DTO for current date.
 *
 * @author <a href="mailto:github@eeel.ru">Artem Utkin</a>
 */
@Builder
@Getter
public class TodayDate {
    /**
     * Current day object.
     */
    private final LocalDate dayObject;
    /**
     * Current day.
     */
    private final int day;
    /**
     * Current month.
     */
    private final int month;
    /**
     * Current year.
     */
    private final int year;
    /**
     * Week start day (MON).
     */
    private final int mondayDay;
    /**
     * Month of start day.
     */
    private final int mondayMonth;
    /**
     * Year of start day.
     */
    private final int mondayYear;
    /**
     * Weekend (SUN).
     */
    private final int sundayDay;
    /**
     * Weekend month.
     */
    private final int sundayMonth;
    /**
     * Weekend year.
     */
    private final int sundayYear;
    /**
     * Day of month start.
     */
    private final int startDay;
    /**
     * Month of "Day of month start".
     */
    private final int startMonth;
    /**
     * Year of "Day of month start".
     */
    private final int startYear;
    /**
     * Day of month end.
     */
    private final int endDay;
    /**
     * Month of "Day of month end".
     */
    private final int endMonth;
    /**
     * Year of "Day of month end".
     */
    private final int endYear;

    /**
     * Auxiliary method for obtaining current day and other common information.
     *
     * @return current day object
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
