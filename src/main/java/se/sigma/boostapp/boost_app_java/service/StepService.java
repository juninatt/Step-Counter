package se.sigma.boostapp.boost_app_java.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public List getStepsByUser(String userId, String startDate, String endDate) {
		if (endDate == null || endDate.equals("")) {
			endDate = LocalDateTime.now().toString();
		}
		return stepRepository.getStepCount(userId, startDate, endDate);
	}

// Get step count per day per multiple users
	public List getStepsByMultipleUsers(List<String> users, String startDate, String endDate) {
		if (endDate == null || endDate.equals("")) {
			endDate = LocalDateTime.now().toString();
		}
		List<List> userStepList = new ArrayList<>();
		for (String s : users) {
			userStepList.add(stepRepository.getStepCount(s, startDate, endDate));
		}
		return userStepList;
	}

}
