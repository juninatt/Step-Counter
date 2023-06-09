package se.pbt.stepcounter.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import se.pbt.stepcounter.controller.apiresponse.GroupedApiResponse;
import se.pbt.stepcounter.controller.apiresponse.OkGetRequest;
import se.pbt.stepcounter.controller.apiresponse.OkPostResponse;
import se.pbt.stepcounter.dto.stepdto.DailyWeekStepDTO;
import se.pbt.stepcounter.dto.stepdto.StepDTO;
import se.pbt.stepcounter.dto.stepdto.WeeklyStepDTO;
import se.pbt.stepcounter.model.MonthStep;
import se.pbt.stepcounter.model.Step;
import se.pbt.stepcounter.model.WeekStep;
import se.pbt.stepcounter.service.StepService;

import javax.validation.Valid;
import java.util.List;

/**
 * A controller for managing steps data for a single user. This class is intended for development purposes only and does not
 * require a security token.
 *
 * @see StepService
 * @see GroupedApiResponse
 */
@RestController
@Profile("dev")
@Validated
@RequestMapping("/steps")
public class StepController {

    private final StepService stepService;


    public StepController(StepService stepService) {
        this.stepService = stepService;
    }

    /**
     * Scheduled task to delete the step table.
     * This method runs at 00:01 (1 minute past midnight) every Monday.
     * It is conditioned by the presence of the "deleting.enabled" property, which defaults to true if missing.
     */
    @ConditionalOnProperty(name = "deleting.enabled", matchIfMissing = true)
    @Scheduled(cron = "1 0 0 * * MON")
    public void deleteStepTable() {
        stepService.deleteStepTable();
    }


    @Operation(summary = "Adds new step data to the database for a specified user")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Step> addStepForUser(final @PathVariable String userId,
                                              final @RequestBody @Valid StepDTO newStepData) {
            var addedStep = stepService.addSingleStepForUser(userId, newStepData);
            return ResponseEntity.ok(addedStep);
    }


    @Operation(summary = "Adds new step data to the database from a list of DTO objects for a specified user")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(value = "/multiple/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Step> addStepListForUser(final @PathVariable String userId,
                                                   final @RequestBody List<@Valid StepDTO> stepDtoList) {
            var addedStep = stepService.addMultipleStepsForUser(userId, stepDtoList);
            return new ResponseEntity<>(addedStep, HttpStatus.CREATED);
    }


    @Operation(summary = "Retrieve the most recently stored step object of the user")
    @OkGetRequest(schemaImplementation = Step.class)
    @GetMapping(value = "/latest/{userId}")
    public ResponseEntity<Step> getUsersLatestStep(final @PathVariable String userId) {
        var retrievedStep = stepService.getLatestStepByStartTimeFromUser(userId);
        return new ResponseEntity<>(retrievedStep, HttpStatus.OK);
    }

    @Operation(summary = "Get a user's step count per month by user and year and month)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/month/{month}"})
    public ResponseEntity<Integer> getUserMonthStepCountForYearAndMonth(final @PathVariable String userId,
                                                        final @PathVariable int year,
                                                        final @PathVariable int month) {
        var monthStepCount =  stepService.getStepCountForUserYearAndMonth(userId, year, month);
        return new ResponseEntity<>(monthStepCount, HttpStatus.OK);
    }

    @Operation(summary = "Get a user's step count per week by user and year and week)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/week/{week}"})
    public ResponseEntity<Integer> getUserWeekStepCountForWeekAndYear(final @PathVariable String userId,
                                                      final @PathVariable int year,
                                                      final @PathVariable int week) {
        var weekStepCount = stepService.getStepCountForUserYearAndWeek(userId, year, week);
        return new ResponseEntity<>(weekStepCount, HttpStatus.OK);
    }

    @Operation(summary = "Get all month-step objects for user from year")
    @OkGetRequest(schemaImplementation = MonthStep.class)
    @GetMapping(value = "/monthsteps/user/{userId}/year/{year}")
    public ResponseEntity<List<MonthStep>> getAllMonthStepsFromYearForUser(final @PathVariable String userId,
                                                           final @PathVariable int year) {
        var listOfMonthSteps = stepService.getMonthStepsFromYearForUser(userId, year);
        return new ResponseEntity<>(listOfMonthSteps, HttpStatus.OK);
    }

    @Operation(summary = "Get all week-steps for user from year")
    @OkGetRequest(schemaImplementation = WeekStep.class)
    @GetMapping(value = "/weeksteps/user/{userId}/year/{year}")
    public ResponseEntity<List<WeekStep>> getAllWeeksStepsFromYearForUser(final @PathVariable String userId,
                                                          final @PathVariable int year) {
        var listOfWeekSteps = stepService.getWeekStepsForUserAndYear(userId, year);
        return new ResponseEntity<>(listOfWeekSteps, HttpStatus.OK);
    }


    @Operation(summary = "Get stepCount per day for current week for a specific user")
    @OkGetRequest(schemaImplementation = DailyWeekStepDTO.class)
    @GetMapping(value = "/stepcount/{userId}/currentweekdaily")
    public ResponseEntity<DailyWeekStepDTO> getStepCountByDayForUserCurrentWeek(final @PathVariable String userId) {
        var stepsPerDayCurrentWeek = stepService.getStepsPerDayForWeek(userId);
        return new ResponseEntity<>(stepsPerDayCurrentWeek, HttpStatus.OK);
    }

    @Operation(summary = "Get stepCount per week for year")
    @OkGetRequest(schemaImplementation = WeeklyStepDTO.class)
    @GetMapping(value = "/stepcount/user/{userId}/year/{year}/weekly")
    public ResponseEntity<WeeklyStepDTO> getStepCountForUserPerWeek(final @PathVariable String userId,
                                                    final @PathVariable int year) {
         var stepsPerWeekForYear = stepService.getStepCountPerWeekForUser(userId, year);
         return new ResponseEntity<>(stepsPerWeekForYear, HttpStatus.OK);
    }
}

