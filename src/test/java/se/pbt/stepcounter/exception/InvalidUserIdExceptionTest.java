package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class InvalidUserIdExceptionTest {

    @Test
    public void testConstructorAndGetter() {
        // Expected values
        var expectedErrorMessage = "User ID cannot be null or empty";
        var expectedHttpStatus = HttpStatus.BAD_REQUEST;

        // Create new exception
        var exception = new InvalidUserIdException(expectedErrorMessage);

        // Actual values
        var actualErrorMessage = exception.getMessage();
        var actualHttpStatus = exception.getStatus();

        Assertions.assertEquals(expectedHttpStatus, actualHttpStatus,
                "HttpStatus was expected to be '" + expectedHttpStatus + "', but was: " + actualHttpStatus);
        Assertions.assertEquals(expectedErrorMessage, actualErrorMessage,
                "Error message was expected to be '" + expectedErrorMessage +"', but was " + actualErrorMessage);
    }
}