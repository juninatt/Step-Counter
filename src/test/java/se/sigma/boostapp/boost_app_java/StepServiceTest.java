package se.sigma.boostapp.boost_app_java;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

import se.sigma.boostapp.boost_app_java.model.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class StepServiceTest {
//
//	@Mock
//	private StepRepository stepRepository;
//
//	@InjectMocks
//	private StepService stepService;
//
//	private List<Step> listOfSteps;
//	private Step step1;
//	private Step step2;
//	private Step step3;
//	private Step step4;
//	private Step step5;
//	private Step step6;
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//
////		Mock database entries
//		step1 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 5000,
//				LocalDateTime.parse("2019-10-21T10:30:00"),
//				LocalDateTime.parse("2019-10-21T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//		step2 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 3000,
//				LocalDateTime.parse("2019-10-22T10:30:00"),
//				LocalDateTime.parse("2019-10-22T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//		step3 = new Step("6896414a-0fb4-47c8-9ec9-22a289ba5612", 1000,
//				LocalDateTime.parse("2019-10-23T10:30:00"),
//				LocalDateTime.parse("2019-10-23T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//		step4 = new Step("590bd5de-1118-4331-b13d-5e763208d3e7", 2000,
//				LocalDateTime.parse("2019-10-21T10:30:00"),
//				LocalDateTime.parse("2019-10-21T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//		step5 = new Step("590bd5de-1118-4331-b13d-5e763208d3e7", 4000,
//				LocalDateTime.parse("2019-10-22T10:30:00"),
//				LocalDateTime.parse("2019-10-22T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//		step6 = new Step("590bd5de-1118-4331-b13d-5e763208d3e7", 8000,
//				LocalDateTime.parse("2019-10-23T10:30:00"),
//				LocalDateTime.parse("2019-10-23T12:30:00"),
//				LocalDateTime.parse("2019-11-08T15:30:00"));
//
////		Mock return values for repository query methods
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612",
//				LocalDateTime.parse("2019-10-21T00:00:00"),
//				LocalDateTime.parse("2019-10-21T23:59:59")))
//		.thenReturn(Arrays.asList(step1));
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612",
//				LocalDateTime.parse("2019-10-22T00:00:00"),
//				LocalDateTime.parse("2019-10-22T23:59:59")))
//		.thenReturn(Arrays.asList(step2));
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612",
//				LocalDateTime.parse("2019-10-23T00:00:00"),
//				LocalDateTime.parse("2019-10-23T23:59:59")))
//		.thenReturn(Arrays.asList(step3));
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"590bd5de-1118-4331-b13d-5e763208d3e7",
//				LocalDateTime.parse("2019-10-21T00:00:00"),
//				LocalDateTime.parse("2019-10-21T23:59:59")))
//		.thenReturn(Arrays.asList(step4));
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"590bd5de-1118-4331-b13d-5e763208d3e7",
//				LocalDateTime.parse("2019-10-22T00:00:00"),
//				LocalDateTime.parse("2019-10-22T23:59:59")))
//		.thenReturn(Arrays.asList(step5));
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"590bd5de-1118-4331-b13d-5e763208d3e7",
//				LocalDateTime.parse("2019-10-23T00:00:00"),
//				LocalDateTime.parse("2019-10-23T23:59:59")))
//		.thenReturn(Arrays.asList(step6));
//
//	}
//
//	@Test
//	public void getAllStepsByUserAndDays_Test() {
//		when(stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612",
//				LocalDateTime.parse("2019-10-21T00:00:00"),
//				LocalDateTime.parse("2019-10-23T23:59:59")))
//		.thenReturn(Arrays.asList(step1, step2, step3));
//
//		assertEquals(9000, stepService.getAllStepsByUserAndDays(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612", "2019-10-21", "2019-10-23"));
//	}
//
//	@Test
//	public void getAllStepsByUserAndDaysAsList_Test() {
//
//		int[] values = {5000, 3000, 1000};
//		List<Integer> expected = Arrays.stream(values).boxed().collect(Collectors.toList());
//		List<Integer> actual = stepService.getAllStepsByUserAndDaysAsList(
//				"6896414a-0fb4-47c8-9ec9-22a289ba5612", "2019-10-21", "2019-10-23");
//
//		assertEquals(expected, actual);
//	}
//
//	@Test
//	public void getStepCountByUsersAndDate() {
//		BulkUsersStepsDTO bulkUsersDto = new BulkUsersStepsDTO(
//				LocalDate.parse("2019-10-21"),
//				LocalDate.parse("2019-10-23"),
//				Arrays.asList("6896414a-0fb4-47c8-9ec9-22a289ba5612", "590bd5de-1118-4331-b13d-5e763208d3e7"));
//
//		int[] firstUserValues = {5000, 3000, 1000};
//		int[] secondUserValues = {2000, 4000, 8000};
//		List<Integer> firstUserExpected = Arrays.stream(firstUserValues).boxed().collect(Collectors.toList());
//		List<Integer> secondUserExpected = Arrays.stream(secondUserValues).boxed().collect(Collectors.toList());
//
//		List<List<Integer>> expected = new ArrayList<>();
//		expected.add(firstUserExpected);
//		expected.add(secondUserExpected);
//
//		List<List<Integer>> actual = stepService.getStepCountByUsersAndDate(bulkUsersDto);
//
//		assertEquals(expected, actual);
//
//	}
//
}
