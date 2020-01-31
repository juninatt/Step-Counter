package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

@RestController
@Profile("prod")
@RequestMapping("/steps")
public class StepController {


	private final StepService stepService;

	public StepController(final StepService stepService) {
		this.stepService = stepService;
	}

	// Post step
	@ApiOperation(value = "Register step entity", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(final @AuthenticationPrincipal Jwt jwt, final @RequestBody StepDTO stepDTO) {
		return stepService.registerSteps((String) jwt.getClaims().get("oid"), stepDTO);
	}

	// Get sum of step count by user ID, start date and end date
	@ApiOperation(value = "Get sum of steps by user ID, start date and end date (optional)", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping("/user/date")
	public int getByUserAndDays(final @AuthenticationPrincipal Jwt jwt, final @RequestParam String startDate,
								final @RequestParam(required = false) String endDate) {
		return stepService.getStepSumByUser((String) jwt.getClaims().get("oid"), startDate, endDate);
	}

	// Get step count per day by user ID and start date
	@ApiOperation(value = "Get a user's step count per day by user ID, start date and end date optional)", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping("/stepcount/{userId}/date")
	public List getUserSteps(final @AuthenticationPrincipal Jwt jwt, final @RequestParam String startDate,
							 final @RequestParam(required = false) String endDate) {
		return stepService.getStepsByUser((String) jwt.getClaims().get("oid"), startDate, endDate);
	}

	// Post userIds and start date to get each of users' step count
	@ApiOperation(value = "Get step count per day for a list of users by start date and end date (optional).", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(value = "/stepcount/bulk/date", produces = MediaType.APPLICATION_JSON_VALUE)
	public List getTheStepsByUsers(final @RequestBody List<String> users, final @RequestParam String startDate,
								   final @RequestParam String endDate) {
		return stepService.getStepsByMultipleUsers(users, startDate, endDate);
	}

}
