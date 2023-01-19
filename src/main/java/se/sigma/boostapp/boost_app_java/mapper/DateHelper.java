package se.sigma.boostapp.boost_app_java.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

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
}