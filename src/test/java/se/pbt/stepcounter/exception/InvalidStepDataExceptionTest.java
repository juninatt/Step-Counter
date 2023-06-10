package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("ValidationFailedException:")
public class InvalidStepDataExceptionTest {

    @Test
    @DisplayName("Test ValidationFailedException Constructor")
    public void testValidationFailedExceptionConstructor() {
        String errorMessage = "Validation failed for step entity";
        InvalidStepDataException exception = new InvalidStepDataException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "HttpStatus is not BAD_REQUEST");
    }
}
