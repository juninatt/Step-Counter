package se.sigma.boostapp.boost_app_java.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.service.StepService;
import se.sigma.boostapp.boost_app_java.util.VerifierJWT;

@RestController
@RequestMapping("/steps")
public class StepController {

	@Autowired
	private StepService stepService;
	
	@Autowired
	private VerifierJWT verifier;
	
	@GetMapping("/get/{stepCount}")
	public ResponseEntity<List<Step>> findByStepCount(@PathVariable int stepCount, @RequestHeader("authorization") String token){
		final List<Step> data = verifier.verifyJwtToken(
				() -> stepService.findByStepCount(stepCount),
				() -> Collections.emptyList(),
				token);
		if (data.isEmpty()) {
			return new ResponseEntity<List<Step>>(data, HttpStatus.FORBIDDEN);
		} else {
			return new ResponseEntity<List<Step>>(data, HttpStatus.OK);
		}
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
