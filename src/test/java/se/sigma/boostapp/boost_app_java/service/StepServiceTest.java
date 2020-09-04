package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository mockedStepRepository;
    @InjectMocks
    private StepService stepService;
    private Step step;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository);
        step = new Step();
    }

    @Test
    public void registerStepsTest() {
       final StepDTO stepDTO = new StepDTO(100,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

       int newStep=200;

       //användare finns inte i databas
       assertNull(step.getUserId());

       //sätter userId och steg
       when(mockedStepRepository.save(any(Step.class))).thenAnswer(new Answer<Step>() {
           @Override
           public Step answer(InvocationOnMock invocationOnMock) throws Throwable {
               Step step1 = (Step) invocationOnMock.getArguments()[0];
               step1.setUserId("userId");
               //100 +200
               step1.setStepCount(stepDTO.getStepCount()+newStep);
               return step1;
           }

       });
       step=stepService.registerSteps(step.getUserId(), stepDTO).get();
        //användare finns i databas nu
        assertNotNull(step.getUserId());
        assertEquals("userId",step.getUserId() );
        //användare har steg
        assertNotNull(step.getStepCount());
        //användare har 300 steg (100+200)
        assertEquals(300, stepService.registerSteps(step.getUserId(), stepDTO).get().getStepCount());
    }

    @Test
    public void getLatestStepTest() {

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
                LocalDateTime.parse("2020-01-02T01:00:00"), LocalDateTime.parse("2020-01-02T01:10:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        List<String> usersList = new ArrayList<>();
        usersList.add("userTest3");
        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);
        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).thenReturn(Optional.of(mockStep));
        when(mockedStepRepository.getAllUsers()).thenReturn(usersList);
        StepDTO stepDto = new StepDTO(50, LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        assertEquals(150,stepService.registerSteps("userTest3", stepDto).get().getStepCount());
    }

/*    //Detta testet är lite knäppt. ELler mycket. Det fyller noll funktion vid närmare eftertanke
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
    }*/
}

