package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;

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

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		stepService = new StepService(mockedStepRepository, mockedMonthStepRepository, mockedWeekStepRepository);
	}

	@Test
	public void registerStepsTest() {
		Step testStep = new Step();
		MonthStep mockMonth = new MonthStep("userId", 2020);
		
		// user is not i databas
		assertNull(testStep.getUserId());
		assertEquals(0,testStep.getStepCount());

		testStep = new Step("userTestId", 100, LocalDateTime.parse("2020-01-02T01:00:00"),
				LocalDateTime.parse("2020-01-02T02:00:00"), LocalDateTime.parse("2020-01-02T03:00:00"));
		
		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
				.thenReturn(Optional.of(testStep));
		when(mockedStepRepository.save(any(Step.class))).thenReturn(testStep);
		when(mockedMonthStepRepository.findFirstByUserIdAndYear(anyString(), anyInt()))
				.thenReturn(Optional.of(mockMonth));
		when(mockedMonthStepRepository.save(any(MonthStep.class))).thenReturn(mockMonth);
		
	
		StepDTO stepDto = new StepDTO(300, LocalDateTime.parse("2020-01-02T02:00:00"),
				LocalDateTime.parse("2020-01-02T03:00:00"), LocalDateTime.parse("2020-01-02T04:00:00"));
		testStep.setStepCount(testStep.getStepCount()+stepDto.getStepCount());
		
		// user is now i databas
				assertNotNull(testStep.getUserId());
				assertEquals(400,testStep.getStepCount());
				

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
		MonthStep mockMonth = new MonthStep("userId", 2020);

		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class)))
				.thenReturn(Optional.of(mockStep));
		when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);
		when(mockedMonthStepRepository.findFirstByUserIdAndYear(anyString(), anyInt()))
				.thenReturn(Optional.of(mockMonth));
		when(mockedMonthStepRepository.save(any(MonthStep.class))).thenReturn(mockMonth);

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

		StepDTO stepDTO3 = new StepDTO(13, LocalDateTime.parse("2020-08-22T00:01:10"),
				LocalDateTime.parse("2020-08-22T01:01:10"), LocalDateTime.parse("2020-08-22T02:01:20"));

		mockStepDTOList.add(stepDTO1);
		mockStepDTOList.add(stepDTO2);
		mockStepDTOList.add(stepDTO3);

		Step mockStep = new Step("idTest", 100, LocalDateTime.parse("2020-08-21T01:00:00"),
				LocalDateTime.parse("2020-08-21T01:00:00"), LocalDateTime.parse("2020-08-21T01:00:00"));

		MonthStep mockMonth = new MonthStep("userId", 2020);

		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString()))
				.thenReturn(Optional.of(mockStep));
		when(mockedStepRepository.save(any())).thenReturn(mockStep);
		when(mockedMonthStepRepository.findFirstByUserIdAndYear(anyString(), anyInt()))
				.thenReturn(Optional.of(mockMonth));
		when(mockedMonthStepRepository.save(any(MonthStep.class))).thenReturn(mockMonth);

		var test = stepService.registerMultipleSteps("idTest", mockStepDTOList);

		assertEquals(122, test.get(0).getStepCount());
		assertEquals(2, test.size());
	}

	@Test
	public void registerMultipleSteps_shouldReturnCompleteListOfStepObjectsForNewUser_test() {
		List<StepDTO> mockStepDTOList2 = new ArrayList<>();
		StepDTO stepDTO1 = new StepDTO(1, LocalDateTime.parse("2020-08-21T03:10:00"),
				LocalDateTime.parse("2020-08-21T03:20:00"), LocalDateTime.parse("2020-08-21T03:25:00"));

		StepDTO stepDTO2 = new StepDTO(2, LocalDateTime.parse("2020-08-22T05:20:00"),
				LocalDateTime.parse("2020-08-22T05:30:00"), LocalDateTime.parse("2020-08-22T05:35:00"));

		StepDTO stepDTO3 = new StepDTO(300, LocalDateTime.parse("2020-08-23T10:10:00"),
				LocalDateTime.parse("2020-08-23T10:20:00"), LocalDateTime.parse("2020-08-23T10:25:00"));

		StepDTO stepDTO4 = new StepDTO(400, LocalDateTime.parse("2020-08-24T11:40:00"),
				LocalDateTime.parse("2020-08-24T11:50:00"), LocalDateTime.parse("2020-08-24T11:55:00"));

		mockStepDTOList2.add(stepDTO2);
		mockStepDTOList2.add(stepDTO1);
		mockStepDTOList2.add(stepDTO3);
		mockStepDTOList2.add(stepDTO4);

		MonthStep mockMonth = new MonthStep("userId", 2020);

		when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString()))
				.thenReturn(Optional.empty());
		when(mockedMonthStepRepository.findFirstByUserIdAndYear(anyString(), anyInt()))
				.thenReturn(Optional.of(mockMonth));
		when(mockedMonthStepRepository.save(any(MonthStep.class))).thenReturn(mockMonth);

		assertEquals(4, stepService.registerMultipleSteps("test", mockStepDTOList2).size());
	}
}