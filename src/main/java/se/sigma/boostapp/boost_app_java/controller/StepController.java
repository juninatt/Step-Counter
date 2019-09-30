package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RestController
@RequestMapping("/steps")
public class StepController {

	@Autowired
	private StepService stepService;
	
	@GetMapping("/get/{stepCount}")
	public List<Step> findByStepCount(@PathVariable int stepCount){
		return stepService.findByStepCount(stepCount);
	}
	
	@GetMapping("/{id}")
	public Optional<Step> getById(@PathVariable long id){
		return stepService.getStepById(id);
	}
	
	@GetMapping
	public Iterable<Step> getAll() {
		return stepService.getAllSteps();
	}
	
	@GetMapping("/start/{startTime}")
	public Iterable<Step> getByStartTime(@PathVariable String startTime) {
		return stepService.findByStartTime(startTime);
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(@RequestBody Step step) {
		return stepService.registerSteps(step);
	}

}
