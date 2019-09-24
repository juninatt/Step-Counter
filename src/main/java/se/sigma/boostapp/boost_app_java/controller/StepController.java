package se.sigma.boostapp.boost_app_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.model.Step;

@RestController
@RequestMapping("/steps")
public class StepController {

	@Autowired 
	private StepRepository stepRepository;
	
	@GetMapping
	public Iterable<Step> getAll() {
		return stepRepository.findAll();
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(@RequestBody Step step) {
		return stepRepository.save(step);
	}
	
	
	
}
