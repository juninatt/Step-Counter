package com.nexergroup.boostapp.java.step.mapper;

import java.time.*;
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
    public static int getWeek(LocalDateTime time) {
        if (time == null) {
            return 0;
        }
        LocalDate date = LocalDate.ofYearDay(time.getYear(), time.getDayOfYear());
        int weekNumber = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return Math.min(weekNumber, 52);
    }

    /**
     * Returns a ZonedDateTime object representing the start of the week for the input LocalDateTime.
     * If the input is null, the method returns null.
     *
     * @param time a LocalDateTime object representing the date for which to find the week start
     * @param zoneId the time zone to use for the ZonedDateTime
     * @return a ZonedDateTime object representing the start of the week
     */
    public static ZonedDateTime getWeekStart(LocalDateTime time, ZoneId zoneId) {
        if (time == null) {
            return null;
        }
        return time.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atZone(zoneId);
    }

    /**
     * Returns a ZonedDateTime object representing the end of the week for the input LocalDateTime.
     * If the input is null, the method returns null.
     *
     * @param time a LocalDateTime object representing the date for which to find the week end
     * @param zoneId the time zone to use for the ZonedDateTime
     * @return a ZonedDateTime object representing the end of the week
     */
    public static ZonedDateTime getWeekEnd(LocalDateTime time, ZoneId zoneId) {
        if (time == null) {
            return null;
        }
        return time.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atZone(zoneId);
    }
}
