package se.sigma.boostapp.boost_app_java.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.Step;
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
	
	public List<Step> findByStartTime(String startTime) {
		return stepRepository.findByStartTime(LocalDateTime.parse(startTime));
	}
	
	public Iterable<Step> getAllSteps() {
		return stepRepository.findAll();
	}
	
	public Step registerSteps(Step step) {
		return stepRepository.save(step);
	}

}
