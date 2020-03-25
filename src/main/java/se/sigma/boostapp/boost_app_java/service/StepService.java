package se.sigma.boostapp.boost_app_java.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.dto.*;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {


	private final StepRepository stepRepository;
	private static final double starPointFactor = 1;

	//remove starPointFactor = 1 after Henriks test
	//private static final double starPointFactor = 0.01;

	public StepService(final StepRepository stepRepository) {
		this.stepRepository = stepRepository;
	}

// Persist a single Step (for 1 or more step count)
	public Optional<Step> registerSteps(String userId, StepDTO stepDto) {
		return Optional.of(stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
					stepDto.getEndTime(), stepDto.getUploadedTime())));
	}

//	Persist multiple Step
	public List<Step> registerMultipleSteps(String userId, List<StepDTO> stepDtoList) {
		List<Step> stepList = new ArrayList<>();
		for (StepDTO stepDTO : stepDtoList) {
			stepList.add(stepRepository.save(new Step(userId, stepDTO.getStepCount(), stepDTO.getStartTime(), stepDTO.getEndTime(), stepDTO.getUploadedTime())));
		}
		return stepList;
	}

//	Get latest step entity by user
	public Optional<Step> getLatestStep(String userId) {
		return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
	}

//	Helper method. Get total step count from a list of Steps
	public int getStepCount(List<Step> steps) {
		int total = 0;
		for (Step step : steps) {
			total += step.getStepCount();
		}
		return total;
	}

//	Get sum of step count by userId, start date and end Date.
	public int getStepSumByUser(String userId, String startDate, String endDate) {
		LocalDateTime end;
		if (endDate == null || endDate.equals("")) {
			end = LocalDateTime.now();
		} else {
			end = LocalDate.parse(endDate).atTime(23, 59, 59);
		}
		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
				LocalDate.parse(startDate).atStartOfDay(), end);
		return getStepCount(allSteps);
	}

// Get step count per day by user ID
	public List<StepDateDTO> getStepsByUser(String userId, String startDate, String endDate) {
		Date firstDate = Date.valueOf(startDate);
		Date lastDate;
		if (endDate == null || endDate.equals("")) {
			lastDate = Date.valueOf(LocalDate.now());
		} else {
			lastDate = Date.valueOf(endDate);
		}
		return stepRepository.getStepCount(userId, firstDate, lastDate);
	}

// Get step count per day per multiple users
	public Optional<List<BulkUsersStepsDTO>> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {

		// String startDate → java.sql.Date firstDate
		Date firstDate = Date.valueOf(startDate);
		Date lastDate;
		if (endDate == null || endDate.equals("")) {
			lastDate = Date.valueOf(LocalDate.now());
		} else {
			lastDate = Date.valueOf(endDate);
		}
		List<String> allUsers = stepRepository.getAllUsers();

		BulkUsersStepsDTO bulkUsersStepsDTO;
		List<BulkUsersStepsDTO> bulkUsersStepsDTOList = new ArrayList<>();

		for (String s : users) {
			if (!allUsers.contains(s)) continue;
			bulkUsersStepsDTO = new BulkUsersStepsDTO(s, stepRepository.getStepCount(s, firstDate, lastDate));
			bulkUsersStepsDTOList.add(bulkUsersStepsDTO);
		}

		return bulkUsersStepsDTOList.isEmpty() ? Optional.empty() : Optional.of(bulkUsersStepsDTOList);
	}

// Translate steps to star points for a list of users
	public List<BulkUserStarPointsDTO> getStarPointsByMultipleUsers(RequestStarPointsDTO requestStarPointsDTO) {

		if (requestStarPointsDTO.getUsers() == null) requestStarPointsDTO.setUsers(stepRepository.getAllUsers());
		return requestStarPointsDTO.getUsers().stream()
				.filter(user -> (stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).isPresent())
				.map(user -> new BulkUserStarPointsDTO(user, new StarPointDateDTO(
						"Steps",
						"Walking",
						requestStarPointsDTO.getStartTime().toString(),
						requestStarPointsDTO.getEndTime().toString(),
						(int) Math.ceil(
								(stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).get()
										* starPointFactor)
				))).collect(Collectors.toList());
	}
}
