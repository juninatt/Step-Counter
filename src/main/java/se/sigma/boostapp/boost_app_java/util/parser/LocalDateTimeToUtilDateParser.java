package se.sigma.boostapp.boost_app_java.util.parser;

import se.sigma.boostapp.boost_app_java.util.BoostAppConverter;

import javax.validation.constraints.NotNull;
import java.time.*;
import java.util.Date;


/**
 * This class is a component that converts a LocalDateTime object into a java.util.Date object.
 */
public class LocalDateTimeToUtilDateParser implements BoostAppConverter<LocalDateTime, java.util.Date> {

    /**
     * Converts the given LocalDateTime object into a java.UtilDate object representing same date and time.
     *
     * @param localDateTime a non-null LocalDateTime object to be converted
     * @return a java.utl.Date object with the given date and time
     */
    @Override
    public Date convert(@NotNull LocalDateTime localDateTime) {
        Date convertedDate = Date.from(Instant.now());
            try {
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
                convertedDate =  Date.from(zonedDateTime.toInstant());
            } catch (DateTimeException | NullPointerException exception) {
                exception.printStackTrace();
        }
        return convertedDate;
    }
}
