package se.sigma.boostapp.boost_app_java.util.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName(" <== LocalDateTimeToUtilDateParser ==>")
class LocalDateTimeToUtilDateParserTest {
    private final LocalDateTimeToUtilDateParser parser = new LocalDateTimeToUtilDateParser();

    @Nested
    @DisplayName("Convert method should return ")
    class ParserShouldReturn {

        @Test
        @DisplayName("an object of java.util.Date.class when input is null")
        void shouldReturnObjectOfJavaUtilDateWhenInputIsNull() {
            LocalDateTime input = null;
            var actual = parser.convert(input);

            assertEquals(java.util.Date.class, actual.getClass());
        }

        @Test
        @DisplayName("an java.util.Date object with the correct date as the input when passed a LocalDateTime object of past date")
        void shouldReturnCorrectDateWhenInputIsFromPast() {
            var input = LocalDateTime.of(1950, 1, 1, 1, 1, 1);
            var expected = Date.from(input.atZone(ZoneId.systemDefault()).toInstant());

            var actual = parser.convert(input);

            assertEquals(expected.getTime(), actual.getTime());
        }

        @Test
        @DisplayName("an java.util.Date object with the correct date as the input when passed a LocalDateTime object of future date")
        void shouldReturnCorrectDateWhenInputIsFromFuture() {
            var input = LocalDateTime.of(2050, 1, 1, 1, 1, 1);
            var expected = Date.from(input.atZone(ZoneId.systemDefault()).toInstant());

            var actual = parser.convert(input);

            assertEquals(expected.getTime(), actual.getTime());
        }
    }
}
