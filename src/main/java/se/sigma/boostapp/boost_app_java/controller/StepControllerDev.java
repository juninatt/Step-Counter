package se.sigma.boostapp.boost_app_java.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.sigma.boostapp.boost_app_java.model.BulkUsersStepsDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.StepDTO;
import se.sigma.boostapp.boost_app_java.model.StepDateDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

import java.util.List;

@RestController
@Profile("dev")
@RequestMapping("/steps")
public class StepControllerDev {


    private final StepService stepService;

    public StepControllerDev(StepService stepService) {
        this.stepService = stepService;
    }

    // Post step
    @ApiOperation(value = "Register step entity", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
            @ApiResponse(code = 401, message = "Request is not authorized"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Step registerSteps(final @PathVariable String userId, final @RequestBody StepDTO stepDTO) {
        return stepService.registerSteps(userId, stepDTO);
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
    public List<BulkUsersStepsDTO> getTheStepsByUsers(final @RequestBody List<String> users, final @RequestParam String startDate,
                                                      final @RequestParam(required = false) String endDate) {
        return stepService.getStepsByMultipleUsers(users, startDate, endDate);
    }
}
