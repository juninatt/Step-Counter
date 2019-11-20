package se.sigma.boostapp.boost_app_java.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {

	@Autowired
	private StepRepository stepRepository;

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

//	Get step count by userId, start date and end Date. 
	public int getAllStepsByUserAndDays(String userId, String startDate, String endDate) {
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

//	Get step count per day as list by userId, start date and end Date. 
	public List<Integer> getAllStepsByUserAndDaysAsList(String userId, String startDate, String endDate) {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end;
		if (endDate == null || endDate.equals("")) {
			end = LocalDate.now();
		} else {
			end = LocalDate.parse(endDate);
		}
		List<Step> userStepsPerDay;
		List<Integer> userStepCountPerDay = new ArrayList<Integer>();
		for (LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
			userStepsPerDay = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
					date.atStartOfDay(), date.atTime(23, 59, 59));
			userStepCountPerDay.add(Integer.valueOf(getStepCount(userStepsPerDay)));
		}
		return userStepCountPerDay;
	}
	
//	Get a list of lists of each user's step count by userId's, start date and end Date.
	public List<List<Integer>> getStepCountByUsersAndDate(BulkUsersStepsDTO bulkDTO) {
		String startTime = bulkDTO.getStartDate().toString();
		String endTime = bulkDTO.getEndDate().toString();
		List<Integer> userStepCount = new ArrayList<>();
		List<List<Integer>> stepList = new ArrayList<>();
		for (String userId : bulkDTO.getUserList()) {
			userStepCount = getAllStepsByUserAndDaysAsList(userId, startTime, endTime);
			stepList.add(userStepCount);
		}
		return stepList;
	}

}
