package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
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

	String userId = "StepTest";
	MonthStep mockMonth;
	WeekStep mockWeek;
	Step mockStep, mockStep2;
	

	@Before
	public void initMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
	}

	@Test
	public void registerStepsTest() {
		Step testStep = new Step();
		MonthStep mockMonth = new MonthStep("userId", 3, 2020, 800);

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

		assertEquals("userTestId", stepService.registerSteps(testStep.getUserId(), stepDto).get().getUserId());

	}

	@Test
	public void getLatestStepTest() {

		final String endDate = "2020-01-02T00:00:00";
		final String userID = "userId";
		final int expectedSteg = 100;

		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
				.thenReturn(Optional.of(new Step("userTest3", 100, LocalDateTime.parse("2020-01-02T00:00:00"),
						LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"))));

		LocalDateTime userEndTime = (stepService.getLatestStep(userID).get().getEnd());

		assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"), userEndTime);
		assertEquals(stepService.getLatestStep(userID).get().getStepCount(), expectedSteg);
	}

	@Test
	public void shouldReturnUpdatedStepCount() {
		Step mockStep = new Step("userTest3", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
				LocalDateTime.parse("2020-01-02T01:10:00"), LocalDateTime.parse("2020-01-02T02:00:00"));
		MonthStep mockMonth = new MonthStep("userId", 2, 2020, 400);

		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
				.thenReturn(Optional.of(mockStep));
		when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);

		StepDTO stepDto = new StepDTO(50, LocalDateTime.parse("2020-01-02T00:00:00"),
				LocalDateTime.parse("2020-01-02T01:20:00"), LocalDateTime.parse("2020-01-02T02:00:00"));
		assertEquals(150, stepService.registerSteps("userTest3", stepDto).get().getStepCount());
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

		var test = stepService.registerMultipleSteps("idTest", mockStepDTOList);

		assertEquals(135, (mockStep.getStepCount()));
	}

	@Test
	public void addStepsToMonthTable_test() {
		mockMonth = new MonthStep(userId, 10, 2020, 800);
		
		when(mockedMonthStepRepository.findByUserIdAndYearAndMonth(userId, 2020, 10))
				.thenReturn(Optional.of(mockMonth));
		int stepSum = mockMonth.getSteps()
				+ mockedMonthStepRepository.findByUserIdAndYearAndMonth(userId, 2020, 10).get().getSteps();

		assertEquals(1600, stepSum);
	}

	@Test
	public void addStepsToWeekTable_test() {
		mockWeek = new WeekStep(userId, 5, 2020, 500);

		when(mockedWeekStepRepository.findByUserIdAndYearAndWeek(userId, 2020, 5)).thenReturn(Optional.of(mockWeek));
		int stepSum = mockWeek.getSteps()
				+ mockedWeekStepRepository.findByUserIdAndYearAndWeek(userId, 2020, 5).get().getSteps();

		assertEquals(1000, stepSum);
	}

	@Test
	public void deleteAllFromStep_test() {

		Step testStep = new Step();

		testStep = new Step("userTestId", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
				LocalDateTime.parse("2020-01-02T02:00:00"), LocalDateTime.parse("2020-01-02T03:00:00"));

		StepDTO stepDto = new StepDTO(300, LocalDateTime.parse("2020-01-02T02:00:00"),
				LocalDateTime.parse("2020-01-02T03:00:00"), LocalDateTime.parse("2020-01-02T04:00:00"));

		// user is now i databas
		assertNotNull(testStep.getUserId());

		Mockito.verify(mockedStepRepository, Mockito.never()).deleteAllFromStep();
		assertEquals(mockedStepRepository.findByUserId("userTestId"), Optional.empty());
	}
	
	@Test
	public void sortListByEndTime_test() {
		
		List<StepDTO> mockStepDTOList = new ArrayList<>();
		StepDTO stepDTO1 = new StepDTO(10, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T02:10:10"), LocalDateTime.parse("2020-08-21T02:01:20"));

		StepDTO stepDTO2 = new StepDTO(12, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T04:01:20"), LocalDateTime.parse("2020-08-21T02:01:20"));

		StepDTO stepDTO3 = new StepDTO(13, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T01:01:10"), LocalDateTime.parse("2020-08-21T02:01:20"));

		mockStepDTOList.add(stepDTO1);
		mockStepDTOList.add(stepDTO2);
		mockStepDTOList.add(stepDTO3);
		
		List<StepDTO> mockStepDTOListReturn= stepService.sortListByEndTime(mockStepDTOList, false);
		
		
		List<StepDTO> mockStepDTOListTest = new ArrayList<>();
		StepDTO stepDTO1Test = new StepDTO(13, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T01:01:10"), LocalDateTime.parse("2020-08-21T02:01:20"));
		
		StepDTO stepDTO2Test = new StepDTO(10, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T02:10:10"), LocalDateTime.parse("2020-08-21T02:01:20"));

		StepDTO stepDTO3Test = new StepDTO(12, LocalDateTime.parse("2020-08-21T02:01:10"),
				LocalDateTime.parse("2020-08-21T04:01:20"), LocalDateTime.parse("2020-08-21T02:01:20"));

		
	
		mockStepDTOListTest.add(stepDTO1Test);
		mockStepDTOListTest.add(stepDTO2Test);
		mockStepDTOListTest.add(stepDTO3Test);
		
		assertEquals(mockStepDTOListTest.getClass(),mockStepDTOListReturn.getClass());
	}
	@Test
	public void getWeekNumber_test() {
		
		LocalDateTime inputDate=LocalDateTime.of(2020, 10, 22, 14, 56); 
		
		int returnWeek=stepService.getWeekNumber(inputDate);
		
		assertEquals(43,returnWeek);
	}
	
	@Test
	public void getStepCountMonth_test() throws NoSuchElementException{
	
		mockMonth = new MonthStep(userId, 10, 2020, 800);
		
		when(mockedMonthStepRepository.findByUserIdAndYearAndMonth(userId, 2020, 10))
				.thenReturn(Optional.of(mockMonth));
		
		int stepFind=mockedMonthStepRepository.findByUserIdAndYearAndMonth(userId, 2020, 10).get().getSteps();
		// steps ar in databas
		assertEquals(800,stepFind);
		//no steps in databas
		mockedMonthStepRepository.findByUserIdAndYearAndMonth(userId, 2020, 11).equals(Optional.of(0));
	}
	
	@Test
	public void getStepCountWeek_test() {
		mockWeek= new WeekStep(userId, 43, 2020, 300);
		when(mockedWeekStepRepository.findByUserIdAndYearAndWeek(userId, 2020, 43))
			.thenReturn(Optional.of(mockWeek));
		
		int weekStepFind=mockedWeekStepRepository.findByUserIdAndYearAndWeek(userId, 2020, 43).get().getSteps();
		//steps find in databas
		assertEquals(300,weekStepFind);
		//no steps i databas
		mockedWeekStepRepository.findByUserIdAndYearAndWeek(userId, 2020, 40).equals(Optional.of(0));
		
	}
}