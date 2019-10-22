package se.sigma.boostapp.boost_app_java.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {
	
	@Autowired 
	private StepRepository stepRepository;
	
	public List<Step> findByStepCount(int stepCount){
		return stepRepository.findByStepCount(stepCount);
	}
	
	public Optional<Step> getStepById(long id){
		return stepRepository.findById(id);
	}
	
	public List<Step> findByUserId(int userId) {
		return stepRepository.findByUserId(userId);
	}
	
	public Iterable<Step> getAllSteps() {
		return stepRepository.findAll();
	}
	
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
		int total = 0;
		for (Step step : allSteps) {
			total += step.getStepCount();
		}
		
		return total;
	}

}
