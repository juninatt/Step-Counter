package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RestController
@RequestMapping("/steps")
public class StepController {

	@Autowired
	private StepService stepService;

	// Get step by step counts
	@ApiOperation(value = "Get step counts", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/get/{stepCount}")
	public List<Step> findByStepCount(@PathVariable int stepCount) {
		return stepService.findByStepCount(stepCount);
	}

	// Get step by ID
	@ApiOperation(value = "Get steps by id", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/{id}")
	public Optional<Step> getById(@PathVariable long id) {
		return stepService.getStepById(id);
	}

	// Get step by user ID
		@ApiOperation(value = "Get steps by user id", response = List.class)
		@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
				@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
				@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
				@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

		@GetMapping("/user/{userId}")
		public List<Step> getByUserId(@PathVariable int userId) {
			return stepService.findByUserId(userId);
		}
	
	// Get all step
	@ApiOperation(value = "Get all", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping
	public Iterable<Step> getAll() {
		return stepService.getAllSteps();
	}

	// Get step by date time
	@ApiOperation(value = "Get step by datetime", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/start/{startTime}")
	public Iterable<Step> getByStartTime(@PathVariable String startTime) {
		return stepService.findByStartTime(startTime);
	}

	// Post step
	@ApiOperation(value = "Register steps", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(@RequestBody StepDTO stepDTO) {
		return stepService.registerSteps(stepDTO);
	}
	
	// Delete step
	@ApiOperation(value = "Delete steps", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted steps"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@DeleteMapping("/{id}")
	public void deleteSteps(@PathVariable long id) {
		stepService.deleteById(id);
	}

}
