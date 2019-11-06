package se.sigma.boostapp.boost_app_java.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.BulkDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {
	
	@Autowired 
	private StepRepository stepRepository;
	
	public Optional<Step> getStepById(long id){
		return stepRepository.findById(id);
	}
	
	public List<Step> findByUserId(int userId) {
		return stepRepository.findByUserId(userId);
	}
	
	public Iterable<Step> getAllSteps() {
		return stepRepository.findAll();
	}
	
	// Persist steps
	public Step registerSteps(StepDTO stepDto) {
		return stepRepository.save(new Step(stepDto.getUserId(),
				stepDto.getStepCount(), 
				stepDto.getStartTime(), 
				stepDto.getEndTime(), 
				stepDto.getUploadedTime()));
	}
	
	public void deleteById(long id) {
		stepRepository.deleteById(id);
	}
	
	public int getAllStepsByUserAndDays(int userId, String startDate, String endDate) {
		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanAndEndTimeLessThan(
				userId, LocalDate.parse(startDate).atStartOfDay(), LocalDate.parse(endDate).atTime(23, 59, 59));
		return getStepCount(allSteps);
	}
	
	public int getAllStepsByUserAndWeek(int userId, String date) {
		LocalDateTime monday = LocalDate.parse(date)
				.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.atStartOfDay();
		LocalDateTime sunday = monday.plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59);
		
		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanAndEndTimeLessThan(
				userId, monday, sunday);
		return getStepCount(allSteps);
	}
	
	public int getAllStepsByUserAndMonth(int userId, String date) {
		LocalDate dateOfMonth = LocalDate.parse(date);
		LocalDateTime firstDay = dateOfMonth.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
		LocalDateTime lastDay = dateOfMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
		
		List<Step> allSteps = stepRepository.findByUserIdAndStartTimeGreaterThanAndEndTimeLessThan(
				userId, firstDay, lastDay);
		return getStepCount(allSteps);
	}
	
	public int getStepCount(List<Step> steps) {
		int total = 0;
		for (Step step : steps) {
			total += step.getStepCount();
		}
		return total;
	}
	
	public int getStepCountByUsersAndDate(BulkDTO bulkDTO) {
		int total = 0;
		LocalDateTime startTime = bulkDTO.getStartTime();
		List<Step> stepList;
		for (Integer userId : bulkDTO.getUserList()) {
			stepList = stepRepository
					.findByUserIdAndStartTimeGreaterThanAndEndTimeLessThan(userId, startTime, LocalDateTime.now());
			total += getStepCount(stepList);
		}
		
		return total;
	}

}
