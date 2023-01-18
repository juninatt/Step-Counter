package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.UserStepListDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import se.sigma.boostapp.boost_app_java.util.Matcher;
import se.sigma.boostapp.boost_app_java.util.Sorter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository mockedStepRepository;

    @Mock
    private WeekStepRepository mockedWeekStepRepository;
    @Mock
    private MonthStepRepository mockedMonthStepRepository;

    @InjectMocks
    private StepService stepService;

    private final String USERID = "StepTest";

    Sorter sorter;

    Matcher matcher;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
        sorter = new Sorter();
        matcher = new Matcher();
    }

    @Test
    public void registerStepsTest() {
        Step testStep = new Step("test", 0, LocalDateTime.now());

        // user is not i databas
        assertEquals(0, testStep.getStepCount());

        testStep = new Step("userTestId", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"), LocalDateTime.parse("2020-01-02T03:00:00"));

        when(mockedStepRepository.save(any(Step.class))).thenReturn(testStep);

        StepDTO stepDto = new StepDTO(300, LocalDateTime.parse("2020-01-02T02:00:00"),
                LocalDateTime.parse("2020-01-02T03:00:00"), LocalDateTime.parse("2020-01-02T04:00:00"));
        testStep.setStepCount(testStep.getStepCount() + stepDto.getStepCount());

        // user is now i databas
        assertNotNull(testStep.getUserId());
        assertEquals(400, testStep.getStepCount());
        var optionalStep = stepService.createOrUpdateStepForUser(testStep.getUserId(), stepDto);
        if (optionalStep.isPresent()) {
            assertEquals("userTestId", optionalStep.get().getUserId());
        } else {
            fail();
        }

    }

    @Test
    public void getLatestStepTest() {

        final String userID = "userId";
        final int expectedSteg = 100;

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
                .thenReturn(Optional.of(new Step("userTest3", 100, LocalDateTime.parse("2020-01-02T00:00:00"),
                        LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"))));

        var optionalStep = stepService.getLatestStepFromUser(userID);
        if (optionalStep.isPresent()) {
            var step = optionalStep.get();
            assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"), step.getEndTime());
            assertEquals(step.getStepCount(), expectedSteg);
        } else {
            fail();
        }
    }

    @Test
    public void shouldReturnUpdatedStepCount() {

        var mockStep = new Step(
                "userTest3",
                100,
                LocalDateTime.of(2020, 1, 1, 1, 0, 0),
                LocalDateTime.of(2020, 1, 1, 2, 0, 0),
                LocalDateTime.of(2020, 1, 1, 3, 0, 0));

        var mockDTO = new StepDTO(
                50,
                LocalDateTime.of(2020, 1, 1, 2, 0, 0),
                LocalDateTime.of(2020, 1, 1, 3, 0, 0),
                LocalDateTime.of(2020, 1, 1, 4, 0, 0));

        when(mockedStepRepository.getListOfStepsByUserId(any(String.class)))
                .thenReturn(Optional.of(List.of(mockStep)));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(mockStep.getUserId()))
                .thenReturn(Optional.of(mockStep));

        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);

        var expectedStep = stepService.createOrUpdateStepForUser("userTest3", mockDTO);

        if (expectedStep.isPresent()) {
            assertEquals(150, expectedStep.get().getStepCount());
        } else {
            fail();
        }
    }

    @Test
    public void registerMultipleSteps_test() {
        List<StepDTO> mockStepDTOList = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(10, LocalDateTime.parse("2020-08-21T02:01:10"),
                LocalDateTime.parse("2020-08-21T02:10:10"), LocalDateTime.parse("2020-08-21T02:20:20"));

        StepDTO stepDTO2 = new StepDTO(12, LocalDateTime.parse("2020-08-21T04:01:20"),
                LocalDateTime.parse("2020-08-21T04:01:20"), LocalDateTime.parse("2020-08-21T04:01:30"));

        StepDTO stepDTO3 = new StepDTO(13, LocalDateTime.parse("2020-08-21T00:01:10"),
                LocalDateTime.parse("2020-08-21T01:01:10"), LocalDateTime.parse("2020-08-21T02:01:20"));

        mockStepDTOList.add(stepDTO1);
        mockStepDTOList.add(stepDTO2);
        mockStepDTOList.add(stepDTO3);

        Step mockStep = new Step("idTest", 100, LocalDateTime.parse("2020-08-21T01:00:00"),
                LocalDateTime.parse("2020-08-21T01:00:00"), LocalDateTime.parse("2020-08-21T01:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString()))
                .thenReturn(Optional.of(mockStep));
        when(mockedStepRepository.save(any())).thenReturn(mockStep);

        stepService.registerMultipleStepsForUser("idTest", mockStepDTOList);

        assertEquals(135, (mockStep.getStepCount()));
    }

    @Test
    public void addStepsToMonthTable_test() {
        var mockMonth = new MonthStep(USERID, 10, 2020, 800);

        when(mockedMonthStepRepository.findByUserIdAndYearAndMonth(USERID, 2020, 10))
                .thenReturn(Optional.of(mockMonth));
        var optionalStep = mockedMonthStepRepository.findByUserIdAndYearAndMonth(USERID, 2020, 10);
        if (optionalStep.isPresent()) {
            assertEquals(1600, mockMonth.getSteps() + optionalStep.get().getSteps());
        } else {
            fail();
        }
    }

    @Test
    public void addStepsToWeekTable_CallsRepositoryAndSavesCorrectSteps() {

        int year = 2020;
        int week = 1;
        int initialSteps = 500;
        int stepsToAdd = 100;
        var mockDTO = new StepDTO(stepsToAdd, null, LocalDateTime.of(2020, 1,1,1,0,0), null);
        int expectedSteps = initialSteps + stepsToAdd;
        var mockWeek = new WeekStep(USERID, week, year, initialSteps);

        when(mockedWeekStepRepository.findByUserIdAndYearAndWeek(USERID, year, week))
                .thenReturn(Optional.of(mockWeek));

        stepService.addStepsToWeekTable(USERID, mockDTO);
        verify(mockedWeekStepRepository).findByUserIdAndYearAndWeek(USERID, year, week);

        ArgumentCaptor<WeekStep> argumentCaptor = ArgumentCaptor.forClass(WeekStep.class);
        verify(mockedWeekStepRepository).save(argumentCaptor.capture());
        WeekStep capturedWeekStep = argumentCaptor.getValue();
        assertEquals(expectedSteps, capturedWeekStep.getSteps());
    }

    @Test
    public void deleteAllFromStep_test() {
        stepService.deleteStepTable();
        verify(mockedStepRepository).deleteAllFromStep();
    }

    @Test
    public void sortListByEndTime_test() {
        List<StepDTO> mockStepDTOList = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(10, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 1, 14, 56), LocalDateTime.of(2020, 10, 1, 14, 56));

        StepDTO stepDTO2 = new StepDTO(12, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 2, 14, 56), LocalDateTime.of(2020, 10, 2, 14, 56));

        StepDTO stepDTO3 = new StepDTO(13, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 3, 14, 56), LocalDateTime.of(2020, 3, 22, 14, 56));

        mockStepDTOList.add(stepDTO1);
        mockStepDTOList.add(stepDTO2);
        mockStepDTOList.add(stepDTO3);

        List<StepDTO> mockStepDTOListTest = new ArrayList<>();
        StepDTO stepDTO1Test = new StepDTO(13, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 3, 14, 56), LocalDateTime.of(2020, 3, 22, 14, 56));

        StepDTO stepDTO2Test = new StepDTO(12, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 2, 14, 56), LocalDateTime.of(2020, 10, 2, 14, 56));

        StepDTO stepDTO3Test = new StepDTO(10, LocalDateTime.of(2020, 10, 1, 14, 56),
                LocalDateTime.of(2020, 10, 1, 14, 56), LocalDateTime.of(2020, 10, 1, 14, 56));

        mockStepDTOListTest.add(stepDTO1Test);
        mockStepDTOListTest.add(stepDTO2Test);
        mockStepDTOListTest.add(stepDTO3Test);

        assertNotEquals(mockStepDTOListTest, mockStepDTOList);

        mockStepDTOListTest = sorter.sortStepDTOListByEndTime(mockStepDTOList);

        assertEquals(mockStepDTOList, mockStepDTOListTest);
    }

    @Test
    public void getWeekNumber_ReturnsCorrectWeek() {

        LocalDateTime inputDate = LocalDateTime.of(2020, 10, 22, 14, 56);

        int returnWeek = matcher.getWeekNumberFromDate(inputDate);

        assertEquals(43, returnWeek);
    }
    @Test
    public void getWeekNumber_ReturnsOneForFirstWeek() {
        LocalDateTime inputDate = LocalDateTime.of(2021, 1, 1, 1, 1);

        int weekNumber = matcher.getWeekNumberFromDate(inputDate);

        assertEquals(1, weekNumber);
    }
    @Test
    public void getWeekNumber_Returns52ForLastWeek() {
        LocalDateTime inputDate = LocalDateTime.of(2022, 12, 30, 23, 59);

        int weekNumber = matcher.getWeekNumberFromDate(inputDate);

        assertEquals(52, weekNumber);
    }

    @Test
    public void getStepCountWeek_ReturnsCorrectSteps() {
        var mockedStepsInWeek = 200;

        when(mockedWeekStepRepository.getStepCountByUserIdYearAndWeek(USERID, 2020, 43))
                .thenReturn(Optional.of(mockedStepsInWeek));

        var optionalStep = stepService.getStepCountForUserYearAndWeek(USERID, 2020, 43);
        assertEquals(Optional.of(mockedStepsInWeek), optionalStep);
    }

    @Test
    public void getStepsByMultipleUsers_ReturnsListWithCorrectSize(){
        List<String> allUsers = new ArrayList<>(List.of("user1", "user2", "user3"));
        List<String> requestedUsers = new ArrayList<>(List.of("user1", "user2"));
        String startDate = "2020-08-23";
        String lastDate = "2020-09-23";

        when(mockedStepRepository.getListOfAllDistinctUserId()).thenReturn(allUsers);

        Optional<List<UserStepListDTO>> result = stepService.getMultipleUserStepListDTOs(requestedUsers, startDate, lastDate);
        if(result.isPresent()){
            assertEquals(2, result.get().size());
        } else {
            fail();
        }
    }

    @Test
    public void getStepsByMultipleUsers_ReturnsOptionalEmpty(){
        List<String> allUsers = new ArrayList<>();
        List<String> requestedUsers = new ArrayList<>(List.of("user1", "user2"));
        String startDate = "2020-08-23";
        String lastDate = "2020-09-23";

        when(mockedStepRepository.getListOfAllDistinctUserId()).thenReturn(allUsers);

        Optional<List<UserStepListDTO>> result = stepService.getMultipleUserStepListDTOs(requestedUsers, startDate, lastDate);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void shouldReturn(){

        var testStep = new Step(USERID, 100, LocalDateTime.of(2021, 9, 21, 14, 56),
                LocalDateTime.of(2021, 9, 21, 15, 56), LocalDateTime.of(2021, 9, 21, 15, 56));

        List<Step> stepList = new ArrayList<>();
        stepList.add(testStep);

        var returnedList = stepService.getListOfStepDataForCurrentWeekFromUser(USERID);
        var expectedResult = 0;

        if(returnedList.isPresent()) {
            assertNotNull(stepList);
            assertEquals(expectedResult, returnedList.get().get(0).getSteps());
        } else {
            fail();
        }
    }

    @Test
    public void getStepCountPerDay_ReturnsListSizeOneAndStepsZero(){

        when(mockedStepRepository.getListOfStepsByUserId(USERID)).thenReturn(Optional.empty());

        var result = stepService.getListOfStepDataForCurrentWeekFromUser(USERID);

        if(result.isPresent()) {
            assertEquals(1, result.get().size());
            assertEquals(0, result.get().get(0).getSteps());
        } else {
            fail();
        }
    }
}