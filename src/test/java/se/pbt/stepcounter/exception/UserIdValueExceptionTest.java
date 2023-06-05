package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class UserIdValueExceptionTest {

    @Test
    public void testUserIdValueException() {
        String errorMessage = "Invalid user ID value";
        UserIdValueException exception = new UserIdValueException(errorMessage);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, exception.getStatus(), "HttpStatus is not NO_CONTENT");
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
    }
}