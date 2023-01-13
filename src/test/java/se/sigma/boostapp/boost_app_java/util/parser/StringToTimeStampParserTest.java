package se.sigma.boostapp.boost_app_java.util.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName(" <== StringToTimeStampParser ==>")
public class StringToTimeStampParserTest {

    private final StringToTimeStampParser parser = new StringToTimeStampParser();
    private final int MARGIN = 1000;

    @Nested
    @DisplayName("Convert method should return: ")
    class parserShouldReturn {

        @Test
        @DisplayName("a time stamp with the same date and time as the input when a valid date-string is passed to method")
        public void shouldReturnCorrectTimeWhenPassedValidDate() {
            var dateTime = LocalDateTime.of(2022, 1, 9, 10, 15);
            var expectedTimestamp = Timestamp.valueOf(dateTime);

            var actualTimestamp = parser.convert("2022-01-09 10:15");

            assertEquals(expectedTimestamp, actualTimestamp);
        }

        @Test
        @DisplayName("a time stamp representing the current date and time when the string passed to the method is invalid")
        public void shouldReturnCurrentTimeWhenPassedInvalidInput() {
            var expectedTimestamp = Timestamp.from(Instant.now());

            var actualTimestamp = parser.convert("invalid input");

            assertEquals(expectedTimestamp.getTime(), actualTimestamp.getTime(), MARGIN);
        }

        @Test
        @DisplayName("a time stamp representing the current date and time when the string passed is not in the format 'yyyy-MM-dd HH:mm'")
        public void shouldReturnCurrentTimeWhenPassedInvalidDateFormat() {
            var expectedTimestamp = Timestamp.from(Instant.now());

            var actualTimestamp = parser.convert("2022-17-02 10:15");

            assertEquals(expectedTimestamp.getTime(), actualTimestamp.getTime(), MARGIN);
        }

        @Test
        @DisplayName("a time stamp representing the current date and time when input")
        public void shouldReturnCurrentTimeWhenPassedNull() {
            var expectedTimestamp = Timestamp.from(Instant.now());

            var actualTimestamp = parser.convert(null);

            assertEquals(expectedTimestamp.getTime(), actualTimestamp.getTime(), MARGIN);
        }
    }
}