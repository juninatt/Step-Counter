package com.nexergroup.boostapp.java.step.service;

import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.testobjects.model.TestStepBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
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

    private final String testUserId = "testUser";

    private final TestStepBuilder testStepBuilder = new TestStepBuilder();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
    }

    @Test
    @DisplayName("Should return a Step object the correct stepCount")
    public void shouldReturnStepWithCorrectStepCount() {
        // Arrange
        var userID = "testUser";
        var mockStep = Optional.of(testStepBuilder.createStepOfFirstMinuteOfYear());

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
                .thenReturn(mockStep);

        // Act
        var result = stepService.getLatestStepFromUser(userID);

        // Expected stepCount
        final int expectedStep = 10;

        // Actual stepCount
        final int actualStepCount = result.getStepCount();

        // Assert
        assertEquals(mockStep.orElseThrow().getEndTime(), result.getEndTime());
        assertEquals(expectedStep, actualStepCount);
    }

    @Test
    @DisplayName("Should call delete-method in repository class")
    public void whenMethodIsCalled_ShouldExecuteRepositoryMethod() {
        stepService.deleteStepTable();
        verify(mockedStepRepository).deleteAllFromStep();
    }
    @Test
    public void getStepCountWeek_ReturnsCorrectSteps() {
        var mockedStepsInWeek = 200;

        when(mockedWeekStepRepository.getStepCountByUserIdYearAndWeek(testUserId, 2020, 43))
                .thenReturn(Optional.of(mockedStepsInWeek));

        var optionalStep = stepService.getStepCountForUserYearAndWeek(testUserId, 2020, 43);
        assertEquals(Optional.of(mockedStepsInWeek), Optional.of(optionalStep));
    }

    @Test
    public void getStepsByMultipleUsers_ReturnsListWithCorrectSize(){
        List<String> allUsers = new ArrayList<>(List.of("user1", "user2", "user3"));
        List<String> requestedUsers = new ArrayList<>(List.of("user1", "user2"));
        String startDate = "2020-08-23";
        String lastDate = "2020-09-23";

        when(mockedStepRepository.getListOfAllDistinctUserId()).thenReturn(allUsers);

        List<BulkStepDateDTO> result = stepService.getListOfUsersStepDataBetweenDates(requestedUsers, startDate, lastDate);
        assertEquals(2, result.size());
    }
}
