package se.sigma.boostapp.boost_app_java.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.exception.NotFoundException;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

import javax.validation.Valid;

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
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully post request"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Step> registerSteps(final @AuthenticationPrincipal Jwt jwt, final @RequestBody @Valid StepDTO stepDTO) {
		return stepService.registerSteps((String) jwt.getClaims().get("oid"), stepDTO).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

	// Post multiple step
	@ApiOperation(value = "Register multiple step entities", response = List.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully post steps"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@PostMapping(value = "/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<Step> registerMultipleSteps(final @AuthenticationPrincipal Jwt jwt, final @RequestBody List<StepDTO> stepDtoList) {
		return stepService.registerMultipleSteps((String) jwt.getClaims().get("oid"), stepDtoList);
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
	@GetMapping("/stepcount/date")
	public List<StepDateDTO> getUserSteps(final @AuthenticationPrincipal Jwt jwt, final @RequestParam String startDate,
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
	public List<BulkUsersStepsDTO> getBulkStepsByUsers(final @RequestBody List<String> users, final @RequestParam String startDate,
													  final @RequestParam(required = false) String endDate) {
		return stepService.getStepsByMultipleUsers(users, startDate, endDate).orElseThrow(() -> new NotFoundException());
	}

	// Get latest step by user
	@ApiOperation(value= "Get user's latest step", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step"),
			@ApiResponse(code = 401, message = "Request is not authorized"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@GetMapping(value = "/latest")
	public ResponseEntity<Step> getLatestStep(final @AuthenticationPrincipal Jwt jwt) {
		return stepService.getLatestStep((String) jwt.getClaims().get("oid")).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<String> errors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String errorMessage = error.getDefaultMessage();
			errors.add(errorMessage);
		});
		return errors;
	}

	@GetMapping("/all")
	public Iterable<Step> getAll() {
		return stepService.findAll();
	}

	@GetMapping("/all/user/{userId}")
	public Iterable<Step> getByUser(@PathVariable String userId) {
		return stepService.findByUserId(userId);
	}
}
