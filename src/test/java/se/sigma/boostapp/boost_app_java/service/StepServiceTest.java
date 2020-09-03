package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository mockedStepRepository;

    private StepService stepService;
    private Step step;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository);
        step = new Step();
    }

    @Test
    public void shouldRegisterStepsSuccessfully() {
        final String userID = "testID";
        final StepDTO stepDTO = new StepDTO(300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        when(mockedStepRepository.save(any(Step.class))).thenReturn(new Step("testID", 300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00")));


        assertEquals(stepDTO.getStepCount(),
                stepService.registerSteps(userID, stepDTO).get().getStepCount());


    }



    @Test
    public void getLatestStep_test() {

        final String endDate = "2020-01-02T00:00:00";
        final String userID = "userId";
        final int expectedSteg = 100;


        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).
                thenReturn(Optional.of(new Step("userTest3", 100,
                        LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                        LocalDateTime.parse("2020-01-02T00:00:00"))));

        LocalDateTime userEndTime = (stepService.getLatestStep(userID).get().getEnd());

        assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"), userEndTime);
        assertEquals(stepService.getLatestStep(userID).get().getStepCount(), expectedSteg);
    }

    @Test
    public void shouldReturnUpdatedStepCount() {

        Step mockStep = new Step("userTest3", 100,
                LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T00:00:00"));

        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);
        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).thenReturn(Optional.of(mockStep));


        StepDTO stepDto = new StepDTO(50, LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        assertEquals(150,stepService.registerSteps("userTest3", stepDto).get().getStepCount());

    }

    //Detta testet är lite knäppt. ELler mycket. Det fyller noll funktion vid närmare eftertanke
    @Test
    public void shouldNotUpdateStepCountShouldCreateNewEntryInDB() {

        Step mockStepExistingInDB = new Step("userTest3", 6000,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T00:00:00"));

        Step mockStep = new Step("userTest3", 100,
                LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T00:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).thenReturn(Optional.of(mockStepExistingInDB));
        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);


        StepDTO stepDto = new StepDTO(100, LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        assertEquals(100,stepService.registerSteps("userTest3", stepDto).get().getStepCount());
    }
}

