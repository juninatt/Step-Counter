package se.sigma.boostapp.boost_app_java.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {

	@Autowired
	private StepRepository stepRepository;

	public Optional<Step> getStepById(long id) {
		return stepRepository.findById(id);
	}

	public List<Step> findByUserId(String userId) {
		return stepRepository.findByUserId(userId);
	}

	public Iterable<Step> getAllSteps() {
		return stepRepository.findAll();
	}

	// Persist steps
	public Step registerSteps(String userId, StepDTO stepDto) {
		return stepRepository.save(new Step(userId, stepDto.getStepCount(), stepDto.getStartTime(),
				stepDto.getEndTime(), stepDto.getUploadedTime()));
	}

	public void deleteById(long id) {
		stepRepository.deleteById(id);
	}

	public int getAllStepsByUserAndDays(String userId, String startDate, String endDate) {
		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
				LocalDate.parse(startDate).atStartOfDay(), LocalDate.parse(endDate).atTime(23, 59, 59));
		return getStepCount(allSteps);
	}
	
	public List<Integer> getAllStepsByUserAndDaysAsList(String userId, String startDate, String endDate) {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		List<Step> userStepsPerDay;
		List<Integer> userStepCountPerDay = new ArrayList<Integer>();
		for (LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
			userStepsPerDay = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
					userId, date.atStartOfDay(), date.atTime(23, 59, 59));
			userStepCountPerDay.add(Integer.valueOf(getStepCount(userStepsPerDay)));
		}
		return userStepCountPerDay;
	}

	public int getAllStepsByUserAndWeek(String userId, String date) {
		LocalDateTime monday = LocalDate.parse(date).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.atStartOfDay();
		LocalDateTime sunday = monday.plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59);

		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
				monday, sunday);
		return getStepCount(allSteps);
	}

	public int getAllStepsByUserAndMonth(String userId, String date) {
		LocalDate dateOfMonth = LocalDate.parse(date);
		LocalDateTime firstDay = dateOfMonth.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
		LocalDateTime lastDay = dateOfMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId,
				firstDay, lastDay);
		return getStepCount(allSteps);
	}

	public int getStepCount(List<Step> steps) {
		int total = 0;
		for (Step step : steps) {
			total += step.getStepCount();
		}
		return total;
	}

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
