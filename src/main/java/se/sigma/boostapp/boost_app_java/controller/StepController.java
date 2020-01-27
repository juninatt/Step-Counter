package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
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

	// Post step
	@ApiOperation(value = "Register step entity", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public Step registerSteps(final @AuthenticationPrincipal Jwt jwt, @RequestBody StepDTO stepDTO) {
		return stepService.registerSteps((String) jwt.getClaims().get("oid"), stepDTO);
	}

	// Post userIds and start time to get each of users' step count
	@ApiOperation(value = "Get step count per day for a list of users. End date is optional (use today's date if end date is missing).", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 400, message = "Request is not authorized"),
			@ApiResponse(code = 404, message = "Error processing request") })

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/stepcount/bulk")
	public List<List<Integer>> getBulkStepCount(@RequestBody BulkUsersStepsDTO bulkDTO) {
		return stepService.getStepCountByUsersAndDate(bulkDTO);
	}

}
