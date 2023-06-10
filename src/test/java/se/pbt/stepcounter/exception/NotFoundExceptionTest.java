package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NotFoundException:")
public class NotFoundExceptionTest {

    @Test
    @DisplayName("Test NotFoundException Constructor with Error Message")
    public void testConstructorWithErrorMessage() {
        String errorMessage = "Error Message";
        NotFoundException exception = new NotFoundException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
    }

    @Test
    @DisplayName("Test NotFoundException Default Constructor")
    public void testDefaultConstructor() {
        NotFoundException exception = new NotFoundException();
        String defaultErrorMessage = "Not found.";
        Assertions.assertEquals(defaultErrorMessage, exception.getMessage(), "Error message is not correct");
    }
}
