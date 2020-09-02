package se.sigma.boostapp.boost_app_java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;


import org.mockito.Mockito;


import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;


import se.sigma.boostapp.boost_app_java.service.StepService;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class StepControllerDevTest {

    @MockBean
    private StepService service;

    private MockMvc mvc;

    ObjectMapper objectMapper = new ObjectMapper();

      @Before
      public void setUp(){
          mvc = MockMvcBuilders.standaloneSetup(new StepControllerDev(service)).build();

      }


    @Test
    public void shouldReturnLatestEntryWithUserId() throws Exception{


        Step step = new Step("testId",300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        when(service.getLatestStep("testId")).thenReturn(Optional.of(step));

        mvc.perform(get("/steps/latest/{userId}" ,"testId"))
                .andDo(print())
                .andExpect(jsonPath("$.userId").value("testId"))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldReturnBadRequestWhenStepCount0() throws Exception{
        objectMapper.registerModule(new JavaTimeModule());

        StepDTO stepDTO = new StepDTO(0,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        mvc.perform(MockMvcRequestBuilders.post("/steps/{userId}", "testId")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8")
                .content(objectMapper.writeValueAsString(stepDTO)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void registerStepsTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        Step mockStep = new Step("testId",
                100,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T00:00:00"));
        StepDTO stepDTO = new StepDTO(300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));
        when(service.registerSteps(Mockito.anyString(),
                Mockito.any(StepDTO.class))).thenReturn(Optional.of(mockStep));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/steps/{userId}", "testId")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stepDTO))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("testId"));
    }
}
