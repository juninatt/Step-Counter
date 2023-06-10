package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("DateTimeValueException:")
public class DateTimeValueExceptionTest {

    @Test
    @DisplayName("Test DateTimeValueException Constructor")
    public void testDateTimeValueExceptionConstructor() {
        String errorMessage = "Invalid date and time value provided";
        DateTimeValueException exception = new DateTimeValueException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus(), "HttpStatus is not CONFLICT");
    }
}
