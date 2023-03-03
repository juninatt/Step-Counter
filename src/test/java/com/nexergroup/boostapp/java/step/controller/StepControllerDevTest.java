package com.nexergroup.boostapp.java.step.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexergroup.boostapp.java.step.builder.BulkStepDateDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.service.StepService;
import com.nexergroup.boostapp.java.step.testobjects.dto.stepdto.TestStepDtoBuilder;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import org.springframework.web.util.NestedServletException;

import java.sql.Date;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    TestStepBuilder testStepBuilder = new TestStepBuilder();

    TestStepDtoBuilder testDTOBuilder = new TestStepDtoBuilder();

    private final String testUserId = "testUser";
    private final String invalidUserId = "invalidId";

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new StepControllerDev(service)).build();
        mockedStepList.add(testStepBuilder.createStepOfFirstMinuteOfYear());
        mockedStepList.add(testStepBuilder.createStepOfSecondMinuteOfYear());
        mockedStepList.add(testStepBuilder.createStepOfThirdMinuteOfYear());
    }

    //Test get latest step by user with valid input
    @Test
    public void shouldReturnLatestEntryWithUserId() throws Exception {
        var step = testStepBuilder.createStepOfFirstMinuteOfYear();

        when(service.getLatestStepFromUser(testUserId)).thenReturn(step);

        mvc.perform(get("/steps/latest/{userId}", testUserId))
                .andDo(print())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(status().isOk());
    }

    //Test registerSteps with stepCount = 0
    @Test
    public void shouldReturnBadRequestWhenStepCount0() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        var stepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
        stepDTO.setStepCount(0);

        mvc.perform(MockMvcRequestBuilders.post("/steps/{userId}", testUserId)
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
        var mockStep = testStepBuilder.createStepOfFirstMinuteOfYear();
        var stepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();

        when(service.addSingleStepForUser(Mockito.anyString(),
                Mockito.any(StepDTO.class))).thenReturn(mockStep);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/steps/{userId}", testUserId)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stepDTO))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains(testUserId));
    }

//    @Test
//    public void getLatestStep_withInvalidUsername_test() throws Exception {
//
//        when(service.getLatestStepFromUser(Mockito.anyString())).thenThrow(NoSuchElementException.class);
//
//        RequestBuilder requestBuilder = MockMvcRequestBuilders
//                .get("/steps/latest/{userId}", "inValidUserID")
//                .accept(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mvc.perform(requestBuilder)
//                .andExpect(status().isNoContent())
//                .andReturn();
//
//        assertThrows(NoSuchElementException.class, () -> {
//            throw Objects.requireNonNull(result.getResolvedException());
//        });
//    }



    @Test
    public void registerMultipleSteps_withValidInput_test() throws Exception {
        List<StepDTO> stepDTOList = new ArrayList<>();
        objectMapper.registerModule(new JavaTimeModule());

        var dto1 = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
        var dto2 = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
        var dto3 = testDTOBuilder.createStepDTOOfThirdMinuteOfYear();

        stepDTOList.add(dto1);
        stepDTOList.add(dto2);
        stepDTOList.add(dto3);

        when(service.addMultipleStepsForUser(Mockito.anyString(), Mockito.anyList())).thenReturn(
                new Step(testUserId, 60, dto1.getStartTime(), dto3.getEndTime(), dto3.getUploadTime()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/steps/multiple/{userId}", testUserId)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(stepDTOList)).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mvc.perform(requestBuilder).andDo(print()).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void getBulkStepsByUsers_WithValidInput_ReturnsStatusOkAndCorrectContent() throws Exception {
        String url = "/steps/stepcount/bulk/date";
        List<String> testUsers = new ArrayList<>(List.of("Test1", "Test2"));
        String startDate = "2021-10-01";
        String endDate = "2021-10-05";

        List<StepDateDTO> stepDateDTOList1 = new ArrayList<>();
        stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-01"), 1, 200L));
        stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-02"), 2, 100L));
        stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-03"), 3, 300L));

        List<StepDateDTO> stepDateDTOList2 = new ArrayList<>();
        stepDateDTOList1.add(new StepDateDTO("Test2", Date.valueOf("2021-10-01"), 1, 200L));
        stepDateDTOList1.add(new StepDateDTO("Test2", Date.valueOf("2021-10-02"), 2, 100L));
        stepDateDTOList1.add(new StepDateDTO("Test2", Date.valueOf("2021-10-03"), 3, 300L));

        List<BulkStepDateDTO> bulkStepListDTODate = new ArrayList<>();
        bulkStepListDTODate.add(new BulkStepDateDTO("Test1", stepDateDTOList1));
        bulkStepListDTODate.add(new BulkStepDateDTO("Test2", stepDateDTOList2));

        when(service.filterUsersAndCreateListOfBulkStepDateDtoWithRange(testUsers, startDate, endDate)).thenReturn((bulkStepListDTODate));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testUsers)).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(bulkStepListDTODate);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    public void getUserMonthSteps_WithValidInput_ReturnsStatusOKAndCorrectSteps() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
        int year = 2021;
        int month = 1;
        int expectedSteps = 1200;

        when(service.getStepCountForUserYearAndMonth(testUserId, year, month)).thenReturn(expectedSteps);
        MvcResult result = mvc.perform(get(url, testUserId, year, month))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    @Disabled
    public void getUserMonthSteps_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
        int year = 2021;
        int month = 1;

        when(service.getStepCountForUserYearAndMonth(invalidUserId, year, month))
                .thenThrow(ValidationFailedException.class);

        mvc.perform(get(url, invalidUserId, year, month))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void getUserWeekStepSteps_WithValidInput_ReturnsStatusOKAndCorrectSteps() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
        int year = 2021;
        int week = 30;
        int expectedSteps = 500;

        when(service.getStepCountForUserYearAndWeek(testUserId, year, week))
                .thenReturn(expectedSteps);

        MvcResult result = mvc.perform(get(url, testUserId, year, week))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }

    @Test
    @Disabled
    public void getUserWeekStepSteps_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
        int year = 2021;
        int week = 30;

        when(service.getStepCountForUserYearAndWeek(invalidUserId, year, week))
                .thenThrow(ValidationFailedException.class);

        mvc.perform(get(url, invalidUserId, year, week))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void getUserWeekStepsList_withValidInput_ReturnsStatusOKAndCorrectContent() throws Exception {
        String url = "/steps/stepcount/{userId}/currentweek";

        List<StepDateDTO> stepDateDTOList = new ArrayList<>();
        stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-06"), 1, 200L));
        stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-07"), 2, 100L));
        stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-08"), 3, 300L));

        var bulkSteps = new BulkStepDateDTOBuilder()
                .withStepList(stepDateDTOList)
                .withUserId(testUserId)
                .build();

        when(service.createBulkStepDateDtoForUserForCurrentWeek(any(String.class))).thenReturn(Optional.of(bulkSteps));

        MvcResult result = mvc.perform(get(url, testUserId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andReturn();

        String actualResponseJson = result.getResponse().getContentAsString();
        String expectedResultJson = objectMapper.writeValueAsString(bulkSteps);

        assertEquals(actualResponseJson, expectedResultJson);
    }

    @Test
    public void getUserWeekStepList_withInValidInput_ReturnsStatusNoContent() throws Exception {
        String url = "/steps/stepcount/{userId}/currentweek";

        when(service.createBulkStepDateDtoForUserForCurrentWeek(invalidUserId)).thenReturn(Optional.empty());

        mvc.perform(get(url, invalidUserId))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}




