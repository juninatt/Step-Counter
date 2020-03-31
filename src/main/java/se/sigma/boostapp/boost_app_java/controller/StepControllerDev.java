package se.sigma.boostapp.boost_app_java.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import se.sigma.boostapp.boost_app_java.dto.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.exception.NotFoundException;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Profile("dev")
@Validated
@RequestMapping("/steps")
public class StepControllerDev {


    private final StepService stepService;

    public StepControllerDev(StepService stepService) {
        this.stepService = stepService;
    }

    // Post step
    @ApiOperation(value = "Register step entity", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully post steps"),
            @ApiResponse(code = 401, message = "Request is not authorized"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Step> registerSteps(final @PathVariable String userId, final @RequestBody @Valid StepDTO stepDTO) {
        return stepService.registerSteps(userId, stepDTO).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    // Post multiple step
    @ApiOperation(value = "Register multiple step entities", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully post steps"),
            @ApiResponse(code = 401, message = "Request is not authorized"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @PostMapping(value = "/multiple/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Step> registerMultipleSteps(final @PathVariable String userId, final @RequestBody List<@Valid StepDTO> stepDtoList) {
        return stepService.registerMultipleSteps(userId, stepDtoList);
    }

    // Get sum of step count by user ID, start date and end date
    @ApiOperation(value = "Get sum of steps by user ID, start date and end date (optional)", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping(value = {"/user/{userId}/date"})
    public int getByUserAndDays(final @PathVariable String userId, final @RequestParam String startDate,
                                final @RequestParam(required = false) String endDate) {
        return stepService.getStepSumByUser(userId, startDate, endDate);
    }

    // Get step count per day by user ID and start date
    @ApiOperation(value = "Get a user's step count per day by user ID, start date and end date optional)", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved step count"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @GetMapping(value = {"/stepcount/{userId}/date"})
    public List<StepDateDTO> getUserSteps(final @PathVariable String userId, final @RequestParam String startDate,
                                          final @RequestParam(required = false) String endDate) {
        return stepService.getStepsByUser(userId, startDate, endDate);
    }

    // Post userIds and start date to get each of users' step count
    @ApiOperation(value = "Get step count per day for a list of users by start date and end date (optional).", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
            @ApiResponse(code = 401, message = "Request is not authorized"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @PostMapping(value = {"/stepcount/bulk/date"}, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(value = "/latest/{userId}")
    public ResponseEntity<Step> getLatestStep(final @PathVariable String userId) {
        return stepService.getLatestStep(userId).map(ResponseEntity::ok).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
