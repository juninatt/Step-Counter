package com.nexergroup.boostapp.java.step.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexergroup.boostapp.java.step.builder.BulkStepDateDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.starpointdto.BulkUserStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.RequestStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.StarPointDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.WeekStepDTO;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.service.StarPointService;
import com.nexergroup.boostapp.java.step.service.StepService;
import com.nexergroup.boostapp.java.step.testobjects.dto.stepdto.TestStepDtoBuilder;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StepApiControllerTest {
    @MockBean
    private StarPointService starPointService;
    @MockBean
    private StepService stepService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StepRepository stepRepository;

    @Nested
    @DisplayName("Step Controller")
    class StepControllerDevTest {
        private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StepControllerDev(stepService)).build();
        private final TestStepBuilder testStepBuilder = new TestStepBuilder();
        private final TestStepDtoBuilder testDTOBuilder = new TestStepDtoBuilder();
        private final String testUserId = "testUser";


        @Test
        @DisplayName("Should return the latest step of a valid user ID")
        void shouldReturnLatestStepOfValidUser() throws Exception {
            // Arrange: Create a Step object for testing
            var testStep = testStepBuilder.createStepOfFirstMinuteOfYear();

            // Set the method responding to the endpoint to return the test Step object when the endpoint is called
            when(stepService.getLatestStepFromUser(testUserId)).thenReturn(testStep);

            // Act: Perform the request to the endpoint and print the result to the console
            mockMvc.perform(get("/steps/latest/{userId}", testUserId))
                    .andDo(print())
                    // Assert: The response has a JSON property called 'userId' equal to the test id
                    // and that the response code is 200(OK)
                    .andExpect(jsonPath("$.userId").value(testUserId))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return a 400 Bad Request status when the step count is 0")
        public void shouldReturnBadRequestWhenStepCount0() throws Exception {
            // Arrange: Register the JavaTimeModule to serialize/deserialize Java 8 date/time types
            objectMapper.registerModule(new JavaTimeModule());

            // Create a StepDTO object for testing with a step count of 0
            var stepDTO = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
            stepDTO.setStepCount(0);

            // Act: Send a POST request to the "/steps/{userId}" endpoint with the test StepDTO as the request body
            // and a user ID of "badId"
            mockMvc.perform(MockMvcRequestBuilders.post("/steps/{userId}", "badId")
                            // Set the request headers to accept JSON and use UTF-8 encoding
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("utf-8")
                            // Set the request body to the serialized JSON representation of the test StepDTO
                            .content(objectMapper.writeValueAsString(stepDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    // Print the response to the console
                    .andDo(print())
                    // Assert that the response status is 400 (Bad Request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should successfully register user steps")
        public void shouldRegisterUserSteps() throws Exception {
            // Set up: create objects needed for the test with the test object builder classes
            objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule with ObjectMapper
            var mockStep = testStepBuilder.createStepOfFirstMinuteOfYear(); // Create mock step for testing
            var stepDTO = testDTOBuilder.createStepDTOOfSecondMinuteOfYear(); // Create StepDTO for testing

            // Mock the addSingleStepForUser method of the step service
            when(stepService.addSingleStepForUser(Mockito.anyString(),
                    Mockito.any(StepDTO.class))).thenReturn(mockStep);

            // Build the request to add the user steps
            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/steps/{userId}", testUserId) // Set the endpoint
                    .accept(MediaType.APPLICATION_JSON) // Set the response content type
                    .content(objectMapper.writeValueAsString(stepDTO)) // Set the request body
                    .contentType(MediaType.APPLICATION_JSON); // Set the request content type

            // Perform the request and get the response
            MvcResult result = mockMvc.perform(requestBuilder).andReturn();
            MockHttpServletResponse response = result.getResponse();

            // Assert that the response is what we expect
            assertEquals(HttpStatus.OK.value(), response.getStatus()); // Assert that the response status is 200 OK
            assertTrue(response.getContentAsString().contains(testUserId)); // Assert that the response body contains the user ID
        }

        @Test
        @DisplayName("Register Multiple Steps with Valid Input Test")
        public void registerMultipleStepsWithValidInput() throws Exception {

            // Create a list of StepDTO objects
            List<StepDTO> stepDTOList = new ArrayList<>();

            // Register JavaTimeModule in objectMapper to serialize/deserialize Java 8 Date/Time API types
            objectMapper.registerModule(new JavaTimeModule());

            // Create three StepDTO objects representing steps taken during the first, second, and third minutes of the year
            var dto1 = testDTOBuilder.createStepDTOOfFirstMinuteOfYear();
            var dto2 = testDTOBuilder.createStepDTOOfSecondMinuteOfYear();
            var dto3 = testDTOBuilder.createStepDTOOfThirdMinuteOfYear();

            // Add the StepDTO objects to the list
            stepDTOList.add(dto1);
            stepDTOList.add(dto2);
            stepDTOList.add(dto3);

            // Mock the stepService to return a Step object when addMultipleStepsForUser is called with any String and the stepDTOList
            when(stepService.addMultipleStepsForUser(Mockito.anyString(), Mockito.anyList())).thenReturn(
                    new Step(testUserId, 60, dto1.getStartTime(), dto3.getEndTime(), dto3.getUploadTime()));

            // Build a POST request to register multiple steps for a user
            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post("/steps/multiple/{userId}", testUserId)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stepDTOList))
                    .characterEncoding("utf-8")
                    .contentType(MediaType.APPLICATION_JSON);

            // Perform the request and print the response to the console
            MvcResult result = mockMvc.perform(requestBuilder).andDo(print()).andReturn();
            MockHttpServletResponse response = result.getResponse();

            // Verify that the response status is OK (200)
            assertEquals(HttpStatus.OK.value(), response.getStatus());
        }

        @Test
        @DisplayName("Test bulk step count retrieval with valid input, returns status OK and correct content")
        public void testGetBulkStepCountWithValidInput() throws Exception {
            // Set up test data
            var requestUrl = "/steps/stepcount/bulk/date";
            List<String> testUserIDs = new ArrayList<>(List.of("TestUser1", "TestUser2"));
            var startDate = "2021-10-01";
            var endDate = "2021-10-05";

            List<StepDateDTO> stepDateDTOList1 = new ArrayList<>();
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-01"), 1, 200L));
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-02"), 2, 100L));
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-03"), 3, 300L));

            List<StepDateDTO> stepDateDTOList2 = new ArrayList<>();
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-01"), 1, 200L));
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-02"), 2, 100L));
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-03"), 3, 300L));

            List<BulkStepDateDTO> bulkStepListDTODate = new ArrayList<>();
            bulkStepListDTODate.add(new BulkStepDateDTO("Test1", stepDateDTOList1));
            bulkStepListDTODate.add(new BulkStepDateDTO("Test2", stepDateDTOList2));

            // Set up mock service response
            when(stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(testUserIDs, startDate, endDate))
                    .thenReturn((bulkStepListDTODate));

            // Build and send the request
            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(requestUrl)
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(testUserIDs)).characterEncoding("utf-8")
                    .contentType(MediaType.APPLICATION_JSON);

            // Verify the response
            MvcResult result = mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andReturn();

            String expectedJsonResponse = objectMapper.writeValueAsString(bulkStepListDTODate);
            String actualJsonResponse = result.getResponse().getContentAsString();

            assertEquals(expectedJsonResponse, actualJsonResponse);
        }


        @Test
        @DisplayName("Given valid input, when calling getBulkStepsByUsers endpoint, then it should return status OK and correct content")
        public void givenValidInput_whenCallingGetBulkStepsByUsersEndpoint_shouldReturnStatusOkAndCorrectContent() throws Exception {
            //Arrange
            String url = "/steps/stepcount/bulk/date";
            List<String> testUsers = new ArrayList<>(List.of("Test1", "Test2"));
            String startDate = "2021-10-01";
            String endDate = "2021-10-05";
            List<StepDateDTO> stepDateDTOList1 = new ArrayList<>();
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-01"), 1, 200L));
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-02"), 2, 100L));
            stepDateDTOList1.add(new StepDateDTO("Test1", Date.valueOf("2021-10-03"), 3, 300L));

            List<StepDateDTO> stepDateDTOList2 = new ArrayList<>();
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-01"), 1, 200L));
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-02"), 2, 100L));
            stepDateDTOList2.add(new StepDateDTO("Test2", Date.valueOf("2021-10-03"), 3, 300L));

            List<BulkStepDateDTO> bulkStepListDTODate = new ArrayList<>();
            bulkStepListDTODate.add(new BulkStepDateDTO("Test1", stepDateDTOList1));
            bulkStepListDTODate.add(new BulkStepDateDTO("Test2", stepDateDTOList2));

            when(stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(testUsers, startDate, endDate)).thenReturn((bulkStepListDTODate));

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(url)
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testUsers))
                    .characterEncoding("utf-8")
                    .contentType(MediaType.APPLICATION_JSON);

            //Act and Assert
            MvcResult result = mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andReturn();

            String expectedJsonResponse = objectMapper.writeValueAsString(bulkStepListDTODate);
            String actualJsonResponse = result.getResponse().getContentAsString();

            assertEquals(expectedJsonResponse, actualJsonResponse);
        }

        @Test
        @DisplayName("Test getUserWeekStepsList with valid input")
        public void getUserWeekStepsList_WithValidInput_ReturnsStatusOkAndCorrectContent() throws Exception {
            // Set up test data and expectations
            String requestUrl = "/steps/stepcount/{userId}/currentweek";
            String testUserId = "TestUser1";
            List<StepDateDTO> stepDateDTOList = new ArrayList<>();
            stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-06"), 1, 200L));
            stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-07"), 2, 100L));
            stepDateDTOList.add(new StepDateDTO(testUserId, Date.valueOf("2020-06-08"), 3, 300L));
            BulkStepDateDTO bulkSteps = new BulkStepDateDTOBuilder()
                    .withStepList(stepDateDTOList)
                    .withUserId(testUserId)
                    .build();
            when(stepService.createBulkStepDateDtoForUserForCurrentWeek(any(String.class)))
                    .thenReturn(Optional.of(bulkSteps));

            // Send the request and check the response
            MvcResult result = mockMvc.perform(get(requestUrl, testUserId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andReturn();
            String actualResponseJson = result.getResponse().getContentAsString();
            String expectedResultJson = objectMapper.writeValueAsString(bulkSteps);
            assertEquals(expectedResultJson, actualResponseJson);
        }

        @Test
        @DisplayName("Test getStepCountByDayForUserAndDate")
        public void testGetStepCountByDayForUserAndDate() throws Exception {
            String userId = "123";

            WeekStepDTO weekStepDTO = new WeekStepDTO(userId, 1, new ArrayList<>(Collections.nCopies(7, 0)));

            // Mock the stepService and set the expected return value
            when(stepService.getStepsPerDayForWeek(userId)).thenReturn(weekStepDTO);

            // Build the request with the correct path variable and request body
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/steps/stepcount/{userId}/currentweekdaily", userId)
                    .contentType(MediaType.APPLICATION_JSON);


            // Perform the request and assert the status code and response body
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.userId").exists())
                    .andExpect(jsonPath("$.weekNumber").exists())
                    .andExpect(jsonPath("$.mondayStepCount").exists())
                    .andExpect(jsonPath("$.tuesdayStepCount").exists())
                    .andExpect(jsonPath("$.wednesdayStepCount").exists())
                    .andExpect(jsonPath("$.thursdayStepCount").exists())
                    .andExpect(jsonPath("$.fridayStepCount").exists())
                    .andExpect(jsonPath("$.saturdayStepCount").exists())
                    .andExpect(jsonPath("$.sundayStepCount").exists());
        }


        @Test
        @DisplayName("getUserWeekStepSteps with invalid input returns HTTP status code 409")
        public void getUserWeekStepSteps_WithInvalidInput_ReturnsStatusConflict() throws Exception {
            // Arrange
            String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
            int year = 2023;
            int week = 10;

            when(stepService.getStepCountForUserYearAndWeek("nonExistingUser", year, week))
                    .thenThrow(ValidationFailedException.class);

            // Act and Assert
            mockMvc.perform(get(url, "nonExistingUser", year, week))
                    .andExpect(status().isConflict())
                    .andReturn();
        }


        /**
         * Tests the behavior of the UserController's getUserWeekStepSteps() method when given valid input.
         */
        @Test
        @DisplayName("getUserWeekStepSteps with valid input returns status OK and correct steps")
        public void getUserWeekStepSteps_WithValidInput_ReturnsStatusOKAndCorrectSteps() throws Exception {
            // Set up test data
            String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
            int year = 2021;
            int week = 30;
            int expectedSteps = 500;

            // Set up mock behavior
            when(stepService.getStepCountForUserYearAndWeek(testUserId, year, week))
                    .thenReturn(expectedSteps);

            // Perform the test
            MvcResult result = mockMvc.perform(get(url, testUserId, year, week))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andReturn();

            // Verify the response
            String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
            String actualJsonResponse = result.getResponse().getContentAsString();

            assertEquals(expectedJsonResponse, actualJsonResponse);
        }


        @Test
        @DisplayName("getUserMonthSteps with invalid input should return status Conflict")
        public void getUserMonthSteps_withInvalidInput_ReturnsStatusConflict() throws Exception {
            String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
            int year = 2021;
            int month = 1;

            when(stepService.getStepCountForUserYearAndMonth("badUser", year, month))
                    .thenThrow(ValidationFailedException.class);

            mockMvc.perform(get(url, "badUser", year, month))
                    .andExpect(status().isConflict())
                    .andReturn();
        }


        @Test
        @DisplayName("Test getUserMonthSteps with valid input returns status OK and correct steps")
        public void testGetUserMonthStepsWithValidInput() throws Exception {
            String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
            int year = 2021;
            int month = 1;
            int expectedSteps = 1200;

            when(stepService.getStepCountForUserYearAndMonth(testUserId, year, month)).thenReturn(expectedSteps);

            MvcResult result = mockMvc.perform(get(url, testUserId, year, month))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andReturn();

            String expectedJsonResponse = objectMapper.writeValueAsString(expectedSteps);
            String actualJsonResponse = result.getResponse().getContentAsString();

            // Verify that the expected and actual JSON responses are equal
            assertEquals(expectedJsonResponse, actualJsonResponse);
        }

        @Test
        @DisplayName("Test getUserWeekStepList with invalid input returns status no content")
        public void getUserWeekStepList_withInValidInput_ReturnsStatusNoContent() throws Exception {
            String url = "/steps/stepcount/{userId}/currentweek";

            when(stepService.createBulkStepDateDtoForUserForCurrentWeek("badUser")).thenReturn(Optional.empty());

            mockMvc.perform(get(url, "badUser"))
                    .andExpect(status().isNoContent())
                    .andReturn();
        }
    }

    @Nested
    @DisplayName("StarPoints Controller")
    class StarPointControllerTest {
        private final MockMvc  mockMvc = MockMvcBuilders.standaloneSetup(new StarPointController(starPointService)).build();


        @Test
        @DisplayName("Given valid input, when getStarPointsByMultipleUsers() is called on StarPointController, then it should return a valid response with correct data")
        public void getStarPointsByUsers_WithValidInput_ReturnsStatusOKandCorrectContent() throws Exception {
            // Initialize necessary variables
            objectMapper.registerModule(new JavaTimeModule());
            var users = new ArrayList<>(List.of("User1", "User2"));
            var startTime = LocalDateTime.parse("2020-08-21T00:01:00").atZone(ZoneId.systemDefault());
            var endTime = LocalDateTime.parse("2021-08-21T00:01:00").atZone(ZoneId.systemDefault());

            // Create a request DTO with the given input
            RequestStarPointsDTO requestStarPointsDTO = new RequestStarPointsDTO(users, startTime, endTime);

            // Create a list of DTOs to use as expected output
            List<BulkUserStarPointsDTO> bulkUserStarPointsDTOList = new ArrayList<>();
            bulkUserStarPointsDTOList.add(new BulkUserStarPointsDTO("User1", new StarPointDateDTO("Steps", "Walking", startTime.toString(), endTime.toString(), 20)));
            bulkUserStarPointsDTOList.add(new BulkUserStarPointsDTO("User2", new StarPointDateDTO("Steps", "Running", startTime.toString(), endTime.toString(), 40)));

            // Set up the mock service to return the expected output
            when(starPointService.getStarPointsByMultipleUsers(any(RequestStarPointsDTO.class))).thenReturn(bulkUserStarPointsDTOList);

            // Build the request to send to the controller
            String appMediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
            String resourcePath = "/starpoints/";
            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(resourcePath)
                    .accept(appMediaType)
                    .content(objectMapper.writeValueAsString(requestStarPointsDTO))
                    .characterEncoding("utf-8")
                    .contentType(appMediaType);

            // Perform the request and assert that the response is correct
            var result = mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(appMediaType))
                    .andReturn();

            String expectedJsonResponse = objectMapper.writeValueAsString(bulkUserStarPointsDTOList);
            String actualJsonResponse = result.getResponse().getContentAsString();

            assertEquals(expectedJsonResponse, actualJsonResponse);
        }
    }
}
