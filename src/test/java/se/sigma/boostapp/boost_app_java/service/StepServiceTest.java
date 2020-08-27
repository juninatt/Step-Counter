package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository stepRepository;

    private StepService stepService;
    private Step step;




    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(stepRepository);
        step= new Step();
    }

    @Test
    public void shouldRegisterStepsSuccessfully(){
        final String userID = "testID";
        final StepDTO stepDTO = new StepDTO(300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00") );

        when(stepRepository.save(any(Step.class))).thenReturn(new Step("testID", 300,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00")));


        assertEquals(stepDTO.getStepCount(),
                stepService.registerSteps(userID, stepDTO).get().getStepCount() );


    }

    /*	public List<Step> registerMultipleSteps(String userId, List<StepDTO> stepDtoList) {
		List<Step> stepList = new ArrayList<>();
		for (StepDTO stepDTO : stepDtoList) {
			stepList.add(stepRepository.save(new Step(userId, stepDTO.getStepCount(), stepDTO.getStartTime(), stepDTO.getEndTime(), stepDTO.getUploadedTime())));
		}
		return stepList;

     */

    @Test
    public void registerMultipleSteps_test(){

// git test
    }

    @Test
    public void getLatestStep_test(){

        final String endDate= "2020-01-02T00:00:00";
        final String userID="userId";
        final int expectedSteg= 100;

        when(stepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).
                        thenReturn(Optional.of(new Step("userTest3", 100,
                        LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                        LocalDateTime.parse("2020-01-02T00:00:00"))));

        LocalDateTime userEndTime= (stepService.getLatestStep(userID).get().getEnd());
        assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"),userEndTime);

        assertEquals(stepService.getLatestStep(userID).get().getStepCount(),expectedSteg);
}

}

