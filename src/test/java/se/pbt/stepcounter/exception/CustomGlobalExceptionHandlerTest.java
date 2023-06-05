package se.pbt.stepcounter.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CustomGlobalExceptionHandler.class)
public class CustomGlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/un-authorized"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testHandleMethodArgumentNotValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/method-argument-not-valid")
                        .param("name", ""))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}


