package se.pbt.stepcounter.exception.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import se.pbt.stepcounter.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(GlobalExceptionHandler.class)
@DisplayName("GlobalExceptionHandler:")
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should return BAD_REQUEST status and correct error message when IllegalArgumentException is handled")
    void testHandleIllegalArgumentException() {
        // Create the exception to be handled
        var exceptionMessage = "IllegalArgumentException message";
        var illegalArgumentException = new IllegalArgumentException(exceptionMessage);

        // Get the response entity by handling the created exception
        var result = handler.handleIllegalArgumentException(illegalArgumentException);
        var responseBody = (ErrorResponse<?>) result.getBody();

        // Assert that the exception returns the correct values
        assertAll(
                () -> assertNotNull(responseBody),
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getStatus()),
                () -> assertEquals(exceptionMessage, responseBody.getMessage())
        );
    }

    @Test
    @DisplayName("Should return NOT_FOUND status and correct error message when NotFoundException is handled")
    public void testHandleNotFoundException() throws Exception {
        // Create the exception to be handled
        var exceptionMessage = "NotFoundException message";
        var notFoundException = new NotFoundException(exceptionMessage);

        // Get the response entity by handling the created exception
        var result = handler.handleNotFoundException(notFoundException);
        var responseBody = (ErrorResponse<?>) result.getBody();

        // Assert that the exception returns the correct values
        assertAll(
                () -> assertNotNull(responseBody),
                () -> assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()),
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.getStatus()),
                () -> assertEquals(exceptionMessage, responseBody.getMessage())
        );
    }
}


