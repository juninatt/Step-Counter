package se.sigma.boostapp.boost_app_java.util.parser;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringToSqlDateParser implements BoostAppConverter<String, Timestamp> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public Timestamp convert(String from) {
        LocalDateTime dateTime = parseInputString(from);
        return dateTime != null && !dateTime.toLocalDate().isLeapYear() ? Timestamp.valueOf(dateTime) : Timestamp.from(Instant.now());
    }

    private LocalDateTime parseInputString(String input) {
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }
}
