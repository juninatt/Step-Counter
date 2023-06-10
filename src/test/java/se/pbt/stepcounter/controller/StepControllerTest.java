package se.pbt.stepcounter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import se.pbt.stepcounter.dto.starpointdto.BulkUserStarPointsDTO;
import se.pbt.stepcounter.dto.starpointdto.RequestStarPointsDTO;
import se.pbt.stepcounter.dto.starpointdto.StarPointDateDTO;
import se.pbt.stepcounter.dto.stepdto.DailyWeekStepDTO;
import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.exception.InvalidStepDataException;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.repository.StepRepository;
import se.pbt.stepcounter.service.StarPointService;
import se.pbt.stepcounter.service.StepService;
import se.pbt.stepcounter.testobjects.TestObjectBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StepControllerTest {
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

        private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StepController(stepService)).build();
        private final TestObjectBuilder testObjectBuilder = new TestObjectBuilder(2023);
        private final String testUserId = "testUser";


        @Nested
        @DisplayName("getUsersLatestStep(): ")
        class GetUserLatestStepTest {

            @Test
            @DisplayName("Returns the latest Step")
            void shouldReturnLatestStepOfValidUser() throws Exception {
                // Arrange
                String testUserId = "testUser";
                Step testStep = new Step();
                testStep.setUserId(testUserId);

                when(stepService.getLatestStepByStartTimeFromUser(testUserId)).thenReturn(testStep);

                // Act & Assert
                mockMvc.perform(get("/steps/latest/{userId}", testUserId))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
                        .andExpect(jsonPath("$.userId").value(testUserId));
            }
        }

        @Nested
        @DisplayName("addStepForUser(): ")
        public class AddStepForUserTest {


            @Test
            @DisplayName("Returns a 400 Bad Request status when the step count is 0")
            public void shouldReturnBadRequestWhenStepCount0() throws Exception {
                // Arrange: Register the JavaTimeModule to serialize/deserialize Java 8 date/time types
                objectMapper.registerModule(new JavaTimeModule());

                // Create a StepDTO object for testing with a step count of 0
                var stepDTO = testObjectBuilder.getTestStepDTO();
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
            @DisplayName("Returns 200 OK and correct userId")
            public void shouldRegisterUserSteps() throws Exception {
                // Set up: create objects needed for the test with the test object builder classes
                objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule with ObjectMapper
                var mockStep = testObjectBuilder.getTestStep(); // Create mock step for testing
                var stepDTO = testObjectBuilder.getTestStepDTO(); // Create StepDTO for testing

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
        }

        @Nested
        @DisplayName("addStepListForUser(): ")
        public class AddStepListForUserTest {

            @Test
            @DisplayName("Returns 201 CREATED when successfully adding steps")
            public void registerMultipleStepsWithValidInput() throws Exception {

                // Create a list of StepDTO objects
                List<StepDTO> stepDTOList = new ArrayList<>();

                // Register JavaTimeModule in objectMapper to serialize/deserialize Java 8 Date/Time API types
                objectMapper.registerModule(new JavaTimeModule());

                // Create three StepDTO objects representing steps taken during the first, second, and third minutes of the year
                var dto1 = testObjectBuilder.getTestStepDTO();
                var dto2 = testObjectBuilder.copyAndPostponeMinutes(dto1, 1);
                var dto3 = testObjectBuilder.copyAndPostponeMinutes(dto2, 1);

                // Add the StepDTO objects to the list
                stepDTOList.add(dto1);
                stepDTOList.add(dto2);
                stepDTOList.add(dto3);

                // Mock the stepService to return a Step object when addStepListForUser is called with any String and the stepDTOList
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
                assertEquals(HttpStatus.CREATED.value(), response.getStatus());
            }
        }

        @Nested
        @DisplayName("getUserWeekStepCountForWeekAndYear(): ")
        public class GetUserWeekStepCountForWeekAndYearTest {

            @Test
            @DisplayName("getUserWeekStepSteps with invalid input returns HTTP status code 400")
            public void getUserWeekStepSteps_WithInvalidInput_ReturnsStatusBadRequest() throws Exception {
                // Arrange
                String url = "/steps/stepcount/{userId}/year/{year}/week/{week}";
                int year = 2023;
                int week = 10;

                when(stepService.getStepCountForUserYearAndWeek("nonExistingUser", year, week))
                        .thenThrow(InvalidStepDataException.class);

                // Act and Assert
                mockMvc.perform(get(url, "nonExistingUser", year, week))
                        .andExpect(status().isBadRequest())
                        .andReturn();
            }


            /**
             * Tests the behavior of the UserController's getUserWeekStepSteps() method when given valid input.
             */
            @Test
            @DisplayName("getUserWeekStep with valid input returns status OK and correct steps")
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
        }

        @Nested
        @DisplayName("getUserMonthStepCountForYearAndMonth(): ")
        public class GetUserMonthStepCountForYearAndMonthTest {

            @Test
            @DisplayName("getUserMonthSteps with invalid input should return status 400")
            public void getUserMonthSteps_withInvalidInput_ReturnsStatusBadRequest() throws Exception {
                String url = "/steps/stepcount/{userId}/year/{year}/month/{month}";
                int year = 2021;
                int month = 1;

                when(stepService.getStepCountForUserYearAndMonth("badUser", year, month))
                        .thenThrow(InvalidStepDataException.class);

                mockMvc.perform(get(url, "badUser", year, month))
                        .andExpect(status().isBadRequest())
                        .andReturn();
            }
        }

        @Nested
        @DisplayName("getAllMonthStepsFromYearForUser(): ")
        public class GetAllMonthStepsFromYearForUserTest {

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
        }

    @Nested
    @DisplayName("getStepCountByDayForUserCurrentWeek(): ")
    class GetStepCountByDayForUserCurrentWeekTest {

        private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new StepController(stepService)).build();

        @Test
        @DisplayName("Returns correct fields and content type")
        public void testGetStepCountByDayForUserAndDate() throws Exception {
            // Set up test data
            String userId = "123";
            DailyWeekStepDTO dailyWeekStepDTO = new DailyWeekStepDTO(userId, 1, new ArrayList<>(Collections.nCopies(7, 0)));

            // Mock the stepService and set the expected return value
            when(stepService.getStepsPerDayForWeek(userId)).thenReturn(dailyWeekStepDTO);

            // Build the request with the correct path variable and request body
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/steps/stepcount/{userId}/currentweekdaily", userId)
                    .contentType(MediaType.APPLICATION_JSON);

            // Perform the request and assert the response
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
        @DisplayName("Test getStepCountByDayForUserCurrentWeek returns WeekStepDTO without null values when input is valid")
        public void testGetStepCountByDayForUserAndDate_whenWeekStepDTONotNull() throws Exception {
            // Set up test data
            String userId = "123";
            DailyWeekStepDTO dailyWeekStepDTO = new DailyWeekStepDTO(userId, 1, new ArrayList<>(Collections.nCopies(7, 0)));

            // Mock the stepService and set the expected return value
            when(stepService.getStepsPerDayForWeek(userId)).thenReturn(dailyWeekStepDTO);

            // Build the request with the correct path variable and request body
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/steps/stepcount/{userId}/currentweekdaily", userId)
                    .contentType(MediaType.APPLICATION_JSON);

            // Perform the request and assert the status code and response body
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.weekNumber").value(1))
                    .andExpect(jsonPath("$.mondayStepCount").value(0))
                    .andExpect(jsonPath("$.tuesdayStepCount").value(0))
                    .andExpect(jsonPath("$.wednesdayStepCount").value(0))
                    .andExpect(jsonPath("$.thursdayStepCount").value(0))
                    .andExpect(jsonPath("$.fridayStepCount").value(0))
                    .andExpect(jsonPath("$.saturdayStepCount").value(0))
                    .andExpect(jsonPath("$.sundayStepCount").value(0));
        }

        @Test
        @DisplayName("GetStepCountCyDayForUserAndDate returns correct values when input is valid")
        public void testGetStepCountByDayForUserAndDateWithValidValues() throws Exception {
            // Set up test data
            String userId = "123";
            List<Integer> weekStepCountByDay = new ArrayList<>(Collections.nCopies(7, 0));
            weekStepCountByDay.set(0, 10);
            weekStepCountByDay.set(1, 20);
            weekStepCountByDay.set(6, 666);
            DailyWeekStepDTO dailyWeekStepDTO = new DailyWeekStepDTO(userId, 1, weekStepCountByDay);

            // Set up mock service
            when(stepService.getStepsPerDayForWeek(userId)).thenReturn(dailyWeekStepDTO);

            // Build the request
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get("/steps/stepcount/{userId}/currentweekdaily", userId)
                    .contentType(MediaType.APPLICATION_JSON);

            // Perform the request and assert the response
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.weekNumber").value(1))
                    .andExpect(jsonPath("$.mondayStepCount").value(10))
                    .andExpect(jsonPath("$.tuesdayStepCount").value(20))
                    .andExpect(jsonPath("$.wednesdayStepCount").value(0))
                    .andExpect(jsonPath("$.thursdayStepCount").value(0))
                    .andExpect(jsonPath("$.fridayStepCount").value(0))
                    .andExpect(jsonPath("$.saturdayStepCount").value(0))
                    .andExpect(jsonPath("$.sundayStepCount").value(666));
        }
        @Test
        @DisplayName("Test getStepCountByDayForUserCurrentWeek when getStepsPerDayForWeek returns null")
        public void testGetStepCountByDayForUserAndDate_whenStepServiceReturnsNull() throws Exception {
            String userId = "123";

            // Mock the stepService and set the expected return value
            when(stepService.getStepsPerDayForWeek(userId)).thenReturn(null);

            // Build the request with the correct path variable and request body
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/steps/stepcount/{userId}/currentweekdaily", userId)
                    .contentType(MediaType.APPLICATION_JSON);

            // Perform the request and assert the status code and response body
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk());
            }
        }
    }

    @Nested
    @DisplayName("StarPointsController:")
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
