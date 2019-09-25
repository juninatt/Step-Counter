package se.sigma.boostapp.boost_app_java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

@Service
public class StepService {
	
	@Autowired
	private StepRepository stepRepository;
	
	public Iterable<Step> getStepsService() {
		return stepRepository.findAll();
	}
	
	public Step saveStepService(Step step) {
		return stepRepository.save(step);
	}
	
}
