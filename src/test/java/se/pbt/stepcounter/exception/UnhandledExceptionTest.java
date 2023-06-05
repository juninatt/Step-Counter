package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("UnhandledException Unit Tests")
public class UnhandledExceptionTest {

    @Test
    @DisplayName("Test UnhandledException Constructor")
    public void testUnhandledExceptionConstructor() {
        String errorMessage = "Unhandled exception occurred while validating step entity";
        UnhandledException exception = new UnhandledException(errorMessage);
        Assertions.assertEquals(errorMessage, exception.getMessage(), "Error message is not correct");
        Assertions.assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatus(), "HttpStatus is not I_AM_A_TEAPOT");
    }
}

