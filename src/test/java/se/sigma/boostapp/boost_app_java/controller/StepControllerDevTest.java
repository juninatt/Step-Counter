package se.sigma.boostapp.boost_app_java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import se.sigma.boostapp.boost_app_java.security.SecurityConfig;
import se.sigma.boostapp.boost_app_java.service.StepService;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
//@WebMvcTest(StepControllerDev.class)
public class StepControllerDevTest {

    @MockBean
    private StepService service;

    @InjectMocks
    private StepControllerDev stepControllerDev;

    private MockMvc mvc;

    ObjectMapper objectMapper = new ObjectMapper();

      @Before
      public void setUp(){
          mvc = MockMvcBuilders.standaloneSetup(stepControllerDev).build();

      }


    @Test
    public void shouldReturnLatestEntryWithUserId() throws Exception{

        //StepService service = mock(StepService.class);

        Step step = new Step("testId",300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        when(service.getLatestStep("testId")).thenReturn(Optional.of(step));

        mvc.perform(get("/steps/latest/{userId}" ,"testId"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void shouldRegisterStepCountSuccessfully() throws Exception{

        StepDTO stepDTO = new StepDTO(300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        Step step = new Step("testId",300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        when(service.registerSteps("testId", stepDTO)).thenReturn(Optional.of(step));

        mvc.perform(post("/steps/{userId}", "testId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stepDTO)))
                .andDo(print())
                .andExpect(status().isOk());

    }
}
