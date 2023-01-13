package se.sigma.boostapp.boost_app_java.util.parser;

import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This class is a component that converts a string representation of a date and time into a Timestamp object.
 * It uses the format "yyyy-MM-dd HH:mm" for parsing the string
 */
@Component
public class StringToTimeStampParser implements BoostAppConverter<String, Timestamp> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Converts the given string representation of a date and time into a Timestamp object.
     *
     * @param dateAsString a non-null string representation of a date and time in the format "yyyy-MM-dd HH:mm"
     * @return a Timestamp object representing the given date and time
     */
    @Override
    public Timestamp convert(@NotNull String dateAsString) {
        var dateTime = toLocalDateTime(dateAsString);
        return Timestamp.valueOf(dateTime);
    }

    /**
     * Parses the given string representation of a date and time into a LocalDateTime object.
     * If the string cannot be parsed, the current date and time is returned.
     *
     * @param dateAsString a string representation of a date and time in the format "yyyy-MM-dd HH:mm"
     * @return a LocalDateTime object representing the given date and time
     */
    private LocalDateTime toLocalDateTime(String dateAsString) {
        var dateAsLocalDateTime = LocalDateTime.now();
        try {
            dateAsLocalDateTime = LocalDateTime.parse(dateAsString, formatter);
        } catch (DateTimeParseException | NullPointerException exception) {
            exception.printStackTrace();
        }
        return dateAsLocalDateTime;
    }
}
