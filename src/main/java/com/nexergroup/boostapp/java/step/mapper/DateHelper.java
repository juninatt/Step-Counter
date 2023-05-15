package com.nexergroup.boostapp.java.step.mapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

/**
 * The DateHelper class contains a single static method that takes a LocalDateTime object as input
 * and returns the week number of the year in which the date falls.
 */
public class DateHelper {

    /**
     * Returns the week number of the year in which the input LocalDateTime falls. If the input is null,
     * the method returns 0.
     *
     * @param time a LocalDateTime object representing the date for which to find the week number
     * @return the week number of the year in which the date falls
     */
    public static int getWeek(ZonedDateTime time) {
        if (time == null) {
            return 0;
        }
        var date = LocalDate.ofYearDay(time.getYear(), time.getDayOfYear());
        var weekNumber = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return Math.min(weekNumber, 52);
    }

    /**
     * Returns a ZonedDateTime object representing the start of the week for the input LocalDateTime.
     * If the input is null, the method returns null.
     *
     * @param dateTime a ZonedDateTime object representing the date for which to find the week start
     * @return a ZonedDateTime object representing the start of the week
     */
    public static ZonedDateTime getWeekStart(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Returns a ZonedDateTime object representing the end of the week for the input LocalDateTime.
     * If the input is null, the method returns null.
     *
     * @param dateTime a ZonedDateTime object representing the date for which to find the week end
     * @return a ZonedDateTime object representing the end of the week
     */
    public static ZonedDateTime getWeekEnd(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
}
