package se.sigma.boostapp.boost_app_java.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.dto.*;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {


	private final StepRepository stepRepository;

	public StepService(final StepRepository stepRepository) {
		this.stepRepository = stepRepository;
	}

// Persist a single Step
	public Step registerSteps(String userId, StepDTO stepDto) {

		return stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
				stepDto.getEndTime(), stepDto.getUploadedTime()));
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
	public List<BulkUsersStepsDTO> getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {
		Date firstDate = Date.valueOf(startDate);
		Date lastDate;
		if (endDate == null || endDate.equals("")) {
			lastDate = Date.valueOf(LocalDate.now());
		} else {
			lastDate = Date.valueOf(endDate);
		}
		BulkUsersStepsDTO bulkUsersStepsDTO;
		List<BulkUsersStepsDTO> bulkUsersStepsDTOList = new ArrayList<>();

		for (String s : users) {
			bulkUsersStepsDTO = new BulkUsersStepsDTO(s, stepRepository.getStepCount(s, firstDate, lastDate));
			bulkUsersStepsDTOList.add(bulkUsersStepsDTO);
		}
		return bulkUsersStepsDTOList;
	}

// Translate steps to star points
	public List<BulkUserStarPointsDTO> getStarPointsByMultipleUsers(List<String> users, String startDate, String endDate) {
		List<BulkUsersStepsDTO> stepsList = getStepsByMultipleUsers(users, startDate, endDate);
		List<BulkUserStarPointsDTO> starPointList = stepsList.stream()
				.map(x -> new BulkUserStarPointsDTO(x.getUserId(), "steps", x.getStepList().stream()
				.map(y -> new StarPointDateDTO(y.getDate(), (long) Math.ceil(y.getSteps() * 0.01)))
						.collect(Collectors.toList())))
				.collect(Collectors.toList());
		return starPointList;
	}
}
