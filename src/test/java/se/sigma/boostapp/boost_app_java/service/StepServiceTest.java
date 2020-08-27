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
import java.util.Optional;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository stepRepository;

    private StepService stepService;



    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(stepRepository);

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

    @Test
    public void shouldNotInsertDataIf_Endtime_of_request_equals_endtime_in_DB(){


    }

    @Test
    public void shouldUpdateStepcountInDB(){

    }

    @Test
    public void shouldNotRegisterANewEntityInDB(){


    }

    @Test
    public void shouldAutowireDependencies(){
        assertNotNull(stepRepository);

    }
}
