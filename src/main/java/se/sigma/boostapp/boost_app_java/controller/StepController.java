package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.sigma.boostapp.boost_app_java.model.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RestController
@RequestMapping("/steps")
public class StepController {

	@Autowired
	private StepService stepService;

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

	@ApiOperation(value = "Get steps by user id", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/user")
	public List<Step> getByUserId(final @AuthenticationPrincipal Jwt jwt) {
		return stepService.findByUserId((String) jwt.getClaims().get("oid"));
	}

	@ApiOperation(value = "Get sum of steps by user id, start date and end date (optional. Use today's date if end date is missing)", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/user/date")
	public int getByUserAndDays(final @AuthenticationPrincipal Jwt jwt, @RequestParam String startDate,
			@RequestParam(required = false) String endDate) {
		return stepService.getAllStepsByUserAndDays((String) jwt.getClaims().get("oid"), startDate, endDate);
	}
	
	@ApiOperation(value = "Get step count per day by user id, start date and end date (optional. Use today's date if end date is missing) as array", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/user/date/bulk")
	public List<Integer> getByUserAndDaysAsList(final @AuthenticationPrincipal Jwt jwt, @RequestParam String startDate,
			@RequestParam(required = false) String endDate) {
		return stepService.getAllStepsByUserAndDaysAsList((String) jwt.getClaims().get("oid"), startDate, endDate);
	}

	@ApiOperation(value = "Get sum of steps by user id and date for week", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/user/week")
	public int getByUserAndWeek(final @AuthenticationPrincipal Jwt jwt, @RequestParam String date) {
		return stepService.getAllStepsByUserAndWeek((String) jwt.getClaims().get("oid"), date);
	}

	@ApiOperation(value = "Get sum of steps by user id and date for month", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/user/month")
	public int getByUserAndMonth(final @AuthenticationPrincipal Jwt jwt, @RequestParam String date) {
		return stepService.getAllStepsByUserAndMonth((String) jwt.getClaims().get("oid"), date);
	}

	// Get all step
	@ApiOperation(value = "Get all steps", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping
	public Iterable<Step> getAll(final @AuthenticationPrincipal Jwt jwt) {
		System.out.println("OID:" + jwt.getClaims().get("oid"));
		return stepService.getAllSteps();
	}

	// Post step
	@ApiOperation(value = "Register step entity", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(final @AuthenticationPrincipal Jwt jwt, @RequestBody StepDTO stepDTO) {
		return stepService.registerSteps((String) jwt.getClaims().get("oid"), stepDTO);
	}

	// Delete step
	@ApiOperation(value = "Delete step entity", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted steps"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@DeleteMapping("/{id}")
	public void deleteSteps(@PathVariable long id) {
		stepService.deleteById(id);
	}
	
	// Post userIds and start time to get each of users' step count
	@ApiOperation(value = "Get step count per day for a list of users", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/stepcount/bulk")
	public List<List<Integer>> getBulkStepCount(@RequestBody BulkUsersStepsDTO bulkDTO) {
		return stepService.getStepCountByUsersAndDate(bulkDTO);
	}

}
