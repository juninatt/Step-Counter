package com.nexergroup.boostapp.java.step.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.util.StepDtoSorter;

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

    StepDtoSorter sorter;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
        sorter = new StepDtoSorter();
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
        var optionalStep = stepService.addSingleStepForUser(testStep.getUserId(), stepDto);
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


        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(mockStep.getUserId()))
                .thenReturn(Optional.of(mockStep));

        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);

        var expectedStep = stepService.addSingleStepForUser("userTest3", mockDTO);

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

        stepService.addMultipleStepsForUser("idTest", mockStepDTOList);

        assertEquals(135, (mockStep.getStepCount()));
    }

    @Test
    public void addStepsToMonthTable_test() {
        var mockMonth = new MonthStep(USERID, 10, 2020, 800);

        when(mockedMonthStepRepository.findByUserIdAndYearAndMonth(USERID, 2020, 10))
                .thenReturn(Optional.of(mockMonth));
        var optionalStep = mockedMonthStepRepository.findByUserIdAndYearAndMonth(USERID, 2020, 10);
        if (optionalStep.isPresent()) {
            assertEquals(1600, mockMonth.getStepCount() + optionalStep.get().getStepCount());
        } else {
            fail();
        }
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

        mockStepDTOListTest = sorter.sortByEndTime(mockStepDTOList);

        assertEquals(mockStepDTOList, mockStepDTOListTest);
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

        Optional<List<BulkStepDateDTO>> result = stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(requestedUsers, startDate, lastDate);
        if(result.isPresent()){
            assertEquals(2, result.get().size());
        } else {
            fail();
        }
    }

    @Test
    public void testCreateBulkStepDateDtoForUserForCurrentWeek_ReturnsZero(){
        // Arrange
        var testStep = new Step(
                USERID,
                100,
                LocalDateTime.of(2021, 9, 21, 14, 56),
                LocalDateTime.of(2021, 9, 21, 15, 56),
                LocalDateTime.of(2021, 9, 21, 15, 56));

        // Act
        var result = stepService.createBulkStepDateDtoForUserForCurrentWeek(USERID);

        // Expected values
        var expectedResult = 0;

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get().getStepList().size());
    }

    @Test
    @DisplayName("Method should return list of size 0 when no data is found in database for userr")
    public void testCreateBulkStepDateDtoForUser_ReturnsListWithSizeZero(){
        // Act
        var result = stepService.createBulkStepDateDtoForUserForCurrentWeek(USERID);

        // Expected values
        var expected = 0;

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get().getStepList().size());
    }
}
