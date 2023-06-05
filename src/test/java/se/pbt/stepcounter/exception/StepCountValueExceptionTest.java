package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("StepCountValueException Unit Tests")
public class StepCountValueExceptionTest {

    @Test
    @DisplayName("Test StepCountValueException Constructor")
    public void testStepCountValueExceptionConstructor() {
        String errorMessage = "Invalid step count value provided";
        StepCountValueException exception = new StepCountValueException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
        Assertions.assertEquals(HttpStatus.NO_CONTENT, exception.getStatus(), "HttpStatus is not NO_CONTENT");
    }
}
