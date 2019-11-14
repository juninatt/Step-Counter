package se.sigma.boostapp.boost_app_java;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class StepServiceTest {
	
	@Mock
	private StepRepository stepRepository;
	
	@InjectMocks
	private StepService stepService;
	
	private List<Step> listOfSteps;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Step step1 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 5000, 
				LocalDateTime.parse("2019-10-21T10:30:00"),
				LocalDateTime.parse("2019-10-21T12:30:00"),
				LocalDateTime.parse("2019-11-08T15:30:00"));
		Step step2 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 3000, 
				LocalDateTime.parse("2019-10-22T10:30:00"),
				LocalDateTime.parse("2019-10-22T12:30:00"),
				LocalDateTime.parse("2019-11-08T15:30:00"));
		Step step3 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 1000, 
				LocalDateTime.parse("2019-10-23T10:30:00"),
				LocalDateTime.parse("2019-10-23T12:30:00"),
				LocalDateTime.parse("2019-11-08T15:30:00"));
		
		listOfSteps = Arrays.asList(step1, step2, step3);
		
		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
				"6896414a-0fb4-47c8-9ec9-22a289ba5612",
				LocalDateTime.parse("2019-10-21T00:00:00"), 
				LocalDateTime.parse("2019-10-23T23:59:59")))
		.thenReturn(listOfSteps);
	}
	
	@Test
	public void getAllStepsByUserAndDays_Test() {
		assertEquals(9000, stepService.getAllStepsByUserAndDays(
				"6896414a-0fb4-47c8-9ec9-22a289ba5612", "2019-10-21", "2019-10-23"));
	}
	
	//TODO: Fix test method
//	@Test
//	public void getAllStepsByUserAndDaysAsList_Test() {
//		int[] values = {5000, 3000, 1000};
//		List<Integer> expected = Arrays.stream(values).boxed().collect(Collectors.toList());
//		List<Integer> actual = stepService.getAllStepsByUserAndDaysAsList(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612", "2019-10-21", "2019-10-23");
//		
//		assertEquals(expected, actual);
//	}
	
}
