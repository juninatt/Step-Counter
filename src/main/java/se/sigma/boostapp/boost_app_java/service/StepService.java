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

	public List<Integer> getStepCountByUsersAndDate(BulkUsersStepsDTO bulkDTO) {
//		int total = 0;

		LocalDateTime startTime = bulkDTO.getStartTime();
		List<Integer> userStepCount = new ArrayList<>();
		List<Step> stepList;
		for (String userId : bulkDTO.getUserList()) {
			stepList = stepRepository.findByUserIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(userId, startTime,
					LocalDateTime.now());
			userStepCount.add(getStepCount(stepList));
		}

		return userStepCount;
	}

	// Decode JWT, get oid
//	public String getJwt(String token) {
//		JsonParser parser = JsonParserFactory.getJsonParser();
//		Map<String, ?> tokenData = parser.parseMap(JwtHelper.decode(token.substring(7)).getClaims());
//		return (String) tokenData.get("oid");
//	}

}
