package se.sigma.boostapp.boost_app_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {
	
	@Autowired 
	private StepRepository stepRepository;
	
	public Iterable<Step> getAllSteps() {
		return stepRepository.findAll();
	}
	
	public Step registerSteps(Step step) {
		return stepRepository.save(step);
	}
}
