package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NotFoundException Unit Tests")
public class NotFoundExceptionTest {

    @Test
    @DisplayName("Test NotFoundException Constructor")
    public void testNotFoundExceptionConstructor() {
        NotFoundException exception = new NotFoundException();
        Assertions.assertEquals("Not found", exception.getMessage(), "Error message is not correct");
    }
}
