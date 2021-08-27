package se.sigma.boostapp.boost_app_java.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.exception.NotFoundException;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.service.StepService;

import javax.validation.Valid;

@RestController
@Profile("prod")
@Validated
@RequestMapping("/steps")
public class StepController {

	private final StepService stepService;

	public StepController(final StepService stepService) {
		this.stepService = stepService;
	}

	/**
	 * Delete step table
	 * 
	 * @throws InterruptedException
	 */
	@ConditionalOnProperty(name = "deleting.enabled", matchIfMissing = true)
	@SuppressWarnings("null")
	// 1=secund , 0=minut, 0= hours, *-dayOfTheMonth *-month MON-Monday
	@Scheduled(cron = "1 0 0 * * MON")
	public void deleteStepTable() throws InterruptedException {
		stepService.deleteStepTabel();
	}

	/**
	 * Post request for step<br>
	 * Register step entity
	 * 
	 * @param jwt     A user
	 * @param stepDTO Data for the steps
	 */
	@ApiOperation(value = "Register step entity", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Step> registerSteps(final @AuthenticationPrincipal Jwt jwt,
			final @RequestBody @Valid StepDTO stepDTO) {
		return stepService.registerSteps((String) jwt.getClaims().get("oid"), stepDTO).map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

	/**
	 * Post request for multiple step <br>
	 * Register multiple step entities
	 * 
	 * @param jwt         A user
	 * @param stepDtoList Data for the list of step
	 * @return
	 */
	@ApiOperation(value = "Register multiple step entities", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post steps"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(value = "/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<StepDTO> registerMultipleSteps(final @AuthenticationPrincipal Jwt jwt,
			final @RequestBody List<@Valid StepDTO> stepDtoList) {
		return stepService.registerMultipleSteps((String) jwt.getClaims().get("oid"), stepDtoList);
	}

	
	/**
	 * Post request <br>
	 * Get step count per day for a list of users by start date and end date
	 * 
	 * @param users     List of userIds
	 * @param startDate Start date as String ("yyyy-[m]m-[d]d")
	 * @param endDate   End date as String ("yyyy-[m]m-[d]d")
	 */
	@ApiOperation(value = "Get step count per day for a list of users by start date and end date (optional).", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(value = "/stepcount/bulk/date", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<BulkUsersStepsDTO> getBulkStepsByUsers(final @RequestBody List<String> users,
			final @RequestParam String startDate, final @RequestParam(required = false) String endDate) {
		return stepService.getStepsByMultipleUsers(users, startDate, endDate)
				.orElseThrow(() -> new NotFoundException());
	}

	/**
	 * Get request <br>
	 * Get user's latest step
	 * 
	 * @param jwt A user
	 */
	@ApiOperation(value = "Get user's latest step", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping(value = "/latest")
	public ResponseEntity<Step> getLatestStep(final @AuthenticationPrincipal Jwt jwt) {
		return stepService.getLatestStep((String) jwt.getClaims().get("oid")).map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}

	/**
	 * Get request <br>
	 * Get a user's step count per month by user and year and month
	 * 
	 * @param jwt   A user
	 * @param year  Actual year
	 * @param month Actual month
	 */
	@ApiOperation(value = "Get a user's step count per month by user and year and month)", response = Integer.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping(value = { "/stepcount/year/{year}/month/{month}" })
	public ResponseEntity<Integer> getUserMonthSteps(final @AuthenticationPrincipal Jwt jwt,
			final @PathVariable int year, final @PathVariable int month) {
		return stepService.getStepCountMonth((String) jwt.getClaims().get("oid"), year, month).map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));

	}

	/**
	 * Get request <br>
	 * Get a user's step count per week by user and year and week
	 * 
	 * @param jwt  A user
	 * @param year Actual year
	 * @param week Actual week
	 */
	@ApiOperation(value = "Get a user's step count per week by user and year and week)", response = Integer.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping(value = { "/stepcount/year/{year}/week/{week}" })

	public ResponseEntity<Integer> getUserWeekSteps(final @AuthenticationPrincipal Jwt jwt,
			final @PathVariable int year, final @PathVariable int week) {

		return stepService.getStepCountWeek((String) jwt.getClaims().get("oid"), year, week).map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}

	/**
	 * Get request <br>
	 * Get list of steps per day per current week
	 * 
	 * @param jwt A user
	 */
	@ApiOperation(value = "Get list of steps per day per current week)", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping(value = { "/stepcount/currentweek" })
	public ResponseEntity<List<StepDateDTO>> getUserWeekSteps(final @AuthenticationPrincipal Jwt jwt) {
		return stepService.getStepCountPerDay((String) jwt.getClaims().get("oid")).map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
	

}
