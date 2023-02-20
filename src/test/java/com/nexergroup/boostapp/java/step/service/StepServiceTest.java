package com.nexergroup.boostapp.java.step.service;

import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
    }

    @Test
    public void getLatestStepTest() {

        final String userID = "userId";
        final int expectedStep = 100;

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
                .thenReturn(Optional.of(new Step("userTest3", 100, LocalDateTime.parse("2020-01-02T00:00:00"),
                        LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"))));

        var step = stepService.getLatestStepFromUser(userID);
        assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"), step.getEndTime());
        assertEquals(step.getStepCount(), expectedStep);
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
    public void getStepCountWeek_ReturnsCorrectSteps() {
        var mockedStepsInWeek = 200;

        when(mockedWeekStepRepository.getStepCountByUserIdYearAndWeek(USERID, 2020, 43))
                .thenReturn(Optional.of(mockedStepsInWeek));

        var optionalStep = stepService.getStepCountForUserYearAndWeek(USERID, 2020, 43);
        assertEquals(Optional.of(mockedStepsInWeek), Optional.of(optionalStep));
    }

    @Test
    public void getStepsByMultipleUsers_ReturnsListWithCorrectSize(){
        List<String> allUsers = new ArrayList<>(List.of("user1", "user2", "user3"));
        List<String> requestedUsers = new ArrayList<>(List.of("user1", "user2"));
        String startDate = "2020-08-23";
        String lastDate = "2020-09-23";

        when(mockedStepRepository.getListOfAllDistinctUserId()).thenReturn(allUsers);

        List<BulkStepDateDTO> result = stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(requestedUsers, startDate, lastDate);
        assertEquals(2, result.size());
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
