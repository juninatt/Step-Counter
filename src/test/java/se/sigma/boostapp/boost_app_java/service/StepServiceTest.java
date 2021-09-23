package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
    }

    @Test
    public void registerStepsTest() {
        Step testStep = new Step();

        // user is not i databas
        assertNull(testStep.getUserId());
        assertEquals(0, testStep.getStepCount());

        testStep = new Step("userTestId", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"), LocalDateTime.parse("2020-01-02T03:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
                .thenReturn(Optional.of(testStep));
        when(mockedStepRepository.save(any(Step.class))).thenReturn(testStep);

        StepDTO stepDto = new StepDTO(300, LocalDateTime.parse("2020-01-02T02:00:00"),
                LocalDateTime.parse("2020-01-02T03:00:00"), LocalDateTime.parse("2020-01-02T04:00:00"));
        testStep.setStepCount(testStep.getStepCount() + stepDto.getStepCount());

        // user is now i databas
        assertNotNull(testStep.getUserId());
        assertEquals(400, testStep.getStepCount());
        var optionalStep = stepService.registerSteps(testStep.getUserId(), stepDto);
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

        var optionalStep = stepService.getLatestStep(userID);
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
        Step mockStep = new Step("userTest3", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T01:10:00"), LocalDateTime.parse("2020-01-02T02:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
                .thenReturn(Optional.of(mockStep));
        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);

        StepDTO stepDto = new StepDTO(50, LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T01:20:00"), LocalDateTime.parse("2020-01-02T02:00:00"));
        var optionalStep = stepService.registerSteps("userTest3", stepDto);
        if (optionalStep.isPresent()) {
            assertEquals(150, optionalStep.get().getStepCount());
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

        stepService.registerMultipleSteps("idTest", mockStepDTOList);

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
        int week = 5;
        int initialSteps = 500;
        int stepsToAdd = 100;
        int expectedSteps = initialSteps + stepsToAdd;
        var mockWeek = new WeekStep(USERID, week, year, initialSteps);

        when(mockedWeekStepRepository.findByUserIdAndYearAndWeek(USERID, year, week))
                .thenReturn(Optional.of(mockWeek));

        stepService.addStepsToWeekTable(year, week, stepsToAdd, USERID);
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

        mockStepDTOListTest = stepService.sortListByEndTime(mockStepDTOList);

        assertEquals(mockStepDTOList, mockStepDTOListTest);
    }

    @Test
    public void getWeekNumber_test() {

        LocalDateTime inputDate = LocalDateTime.of(2020, 10, 22, 14, 56);

        int returnWeek = stepService.getWeekNumber(inputDate);

        assertEquals(43, returnWeek);
    }

    @Test
    public void getStepCountMonth_ReturnsCorrectSteps() {
        var mockStepInMonth = 800;

        when(mockedMonthStepRepository.getStepCountMonth(USERID, 2020, 10))
                .thenReturn(Optional.of(mockStepInMonth));

        var optionalStep = stepService.getStepCountMonth(USERID, 2020, 10);
        assertEquals(Optional.of(mockStepInMonth), optionalStep);
    }

    @Test
    public void getStepCountWeek_ReturnsCorrectSteps() {
        var mockedStepsInWeek = 200;

        when(mockedWeekStepRepository.getStepCountWeek(USERID, 2020, 43))
                .thenReturn(Optional.of(mockedStepsInWeek));

        var optionalStep = stepService.getStepCountWeek(USERID, 2020, 43);
        assertEquals(Optional.of(mockedStepsInWeek), optionalStep);
    }

    @Test
    public void getStepsByMultipleUsers_ReturnsListWithCorrectSize(){
        List<String> allUsers = new ArrayList<>(List.of("user1", "user2", "user3"));
        List<String> requestedUsers = new ArrayList<>(List.of("user1", "user2"));
        String startDate = "2020-08-23";
        String lastDate = "2020-09-23";

        when(mockedStepRepository.getAllUsers()).thenReturn(allUsers);

        Optional<List<BulkUsersStepsDTO>> result = stepService.getStepsByMultipleUsers(requestedUsers, startDate, lastDate);
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

        when(mockedStepRepository.getAllUsers()).thenReturn(allUsers);

        Optional<List<BulkUsersStepsDTO>> result = stepService.getStepsByMultipleUsers(requestedUsers, startDate, lastDate);
        assertEquals(Optional.empty(), result);
    }
    
    @Test
    public void getStepCountPerDay_ReturnsListWithCorrectSizeAndDayOfWeek(){

        List<Step> stepList = new ArrayList<>();

        Step step1 = new Step(USERID, 100, LocalDateTime.of(2021, 9, 21, 14, 56),
                LocalDateTime.of(2021, 9, 21, 15, 56), LocalDateTime.of(2021, 9, 21, 15, 56));

        Step step2 = new Step(USERID, 50, LocalDateTime.of(2021, 9, 22, 14, 56),
                LocalDateTime.of(2021, 9, 22, 15, 56), LocalDateTime.of(2021, 9, 22, 15, 56));

        stepList.add(step1);
        stepList.add(step2);

        when(mockedStepRepository.findByUserId(USERID)).thenReturn(Optional.of(stepList));

        var result = stepService.getStepCountPerDay(USERID);

        if(result.isPresent()) {
            assertEquals(2, result.get().size());
            assertEquals(3, result.get().get(0).getDayOfWeek());
            assertEquals(4, result.get().get(1).getDayOfWeek());
        } else {
            fail();
        }
    }

    @Test
    public void getStepCountPerDay_ReturnsListSizeOneAndStepsZero(){

        when(mockedStepRepository.findByUserId(USERID)).thenReturn(Optional.empty());

        var result = stepService.getStepCountPerDay(USERID);

        if(result.isPresent()) {
            assertEquals(1, result.get().size());
            assertEquals(0, result.get().get(0).getSteps());
        } else {
            fail();
        }
    }
}