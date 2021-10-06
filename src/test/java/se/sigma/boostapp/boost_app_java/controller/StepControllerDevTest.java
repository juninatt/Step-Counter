package se.sigma.boostapp.boost_app_java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.service.StepService;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
public class StepControllerDevTest {

    @MockBean
    private StepService service;

    private MockMvc mvc;

    ObjectMapper objectMapper = new ObjectMapper();

    List<Step> mockedStepList = new ArrayList<>();

    private final String userId = "testId";
    private final String invalidUserId = "invalidId";

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new StepControllerDev(service)).build();
        mockedStepList.add(new Step("testId", 10,
                LocalDateTime.parse("2020-08-21T00:01:00"),
                LocalDateTime.parse("2020-08-21T01:01:10"),
                LocalDateTime.parse("2020-08-21T02:01:20")));
        mockedStepList.add(new Step("testId", 20,
                LocalDateTime.parse("2020-08-22T00:01:00"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20")));
        mockedStepList.add(new Step("testId", 30,
                LocalDateTime.parse("2020-08-22T00:01:00"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20")));
    }

    //Test get latest step by user with valid input
    @Test
    public void shouldReturnLatestEntryWithUserId() throws Exception {
        Step step = new Step("testId", 300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        when(service.getLatestStep("testId")).thenReturn(Optional.of(step));

        mvc.perform(get("/steps/latest/{userId}", "testId"))
                .andDo(print())
                .andExpect(jsonPath("$.userId").value("testId"))
                .andExpect(status().isOk());
    }

    //Test registerSteps with stepCount = 0
    @Test
    public void shouldReturnBadRequestWhenStepCount0() throws Exception {
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

    //Test registerSteps with valid input
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

    @Test
    public void getLatestStep_withInvalidUsername_test() throws Exception {

        when(service.getLatestStep(Mockito.anyString())).thenReturn(Optional.empty());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/steps/latest/{userId}", "inValidUserID")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void registerMultipleSteps_withValidInput_test() throws Exception {
        List<StepDTO> stepDTOList = new ArrayList<>();
        objectMapper.registerModule(new JavaTimeModule());

        StepDTO stepDTO1 = new StepDTO(10, LocalDateTime.parse("2020-08-21T00:01:00"),
                LocalDateTime.parse("2020-08-21T01:01:10"),
                LocalDateTime.parse("2020-08-21T02:01:20"));

        StepDTO stepDTO2 = new StepDTO(10, LocalDateTime.parse("2020-08-22T00:01:00"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20"));

        StepDTO stepDTO3 = new StepDTO(10, LocalDateTime.parse("2020-08-22T00:01:00"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20"));

        stepDTOList.add(stepDTO1);
        stepDTOList.add(stepDTO2);
        stepDTOList.add(stepDTO3);

        when(service.registerMultipleSteps(Mockito.anyString(), Mockito.anyList())).thenReturn(stepDTOList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/steps/multiple/{userId}", "testId")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stepDTOList)).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mvc.perform(requestBuilder).andDo(print()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void getUserMonthSteps_WithValidInput_ReturnsStatusOKAndCorrectSteps() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
        int year = 2021;
        int month = 1;
        int expectedSteps = 1200;

        when(service.getStepCountMonth(userId, year, month)).thenReturn(Optional.of(expectedSteps));
        MvcResult result = mvc.perform(get(url, userId, year, month))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    public void getUserMonthSteps_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
        int year = 2021;
        int month = 1;

        when(service.getStepCountMonth(invalidUserId, year, month))
                .thenReturn(Optional.empty());

        mvc.perform(get(url, invalidUserId, year, month))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void getUserWeekStepSteps_WithValidInput_ReturnsStatusOKAndCorrectSteps() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
        int year = 2021;
        int week = 30;
        int expectedSteps = 500;

        when(service.getStepCountWeek(userId, year, week))
                .thenReturn(Optional.of(expectedSteps));

        MvcResult result = mvc.perform(get(url, userId, year, week))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    public void getUserWeekStepSteps_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
        int year = 2021;
        int week = 30;

        when(service.getStepCountWeek(invalidUserId, year, week))
                .thenReturn(Optional.empty());

        mvc.perform(get(url, invalidUserId, year, week))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void getUserWeekStepsList_withValidInput_ReturnsStatusOKAndCorrectContent() throws Exception {
        String url = "/steps/stepcount/{userId}/currentweek";

        List<StepDateDTO> stepDateDTOList = new ArrayList<>();
        stepDateDTOList.add(new StepDateDTO(userId, Date.valueOf("2020-06-06"), 1, 200L));
        stepDateDTOList.add(new StepDateDTO(userId, Date.valueOf("2020-06-07"), 2, 100L));
        stepDateDTOList.add(new StepDateDTO(userId, Date.valueOf("2020-06-08"), 3, 300L));

        when(service.getStepCountPerDay(any(String.class))).thenReturn(Optional.of(stepDateDTOList));

        MvcResult result = mvc.perform(get(url, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();

        String actualResponseJson = result.getResponse().getContentAsString();
        String expectedResultJson = objectMapper.writeValueAsString(stepDateDTOList);

        assertEquals(actualResponseJson, expectedResultJson);
    }

    @Test
    public void getUserWeekStepList_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/currentweek";

        when(service.getStepCountPerDay(invalidUserId)).thenReturn(Optional.empty());

        mvc.perform(get(url, invalidUserId))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}




