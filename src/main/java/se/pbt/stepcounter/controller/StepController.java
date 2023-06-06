package se.pbt.stepcounter.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
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
     * Deletes the step table.
     * This method is scheduled to run at 00:01 (1 minute past midnight) every Monday.
     * It is conditioned by the presence of the "deleting.enabled" property, which defaults to true if missing.
     * If the property is present and set to true, the step table will be deleted by invoking the {@link StepService#deleteStepTable()} method.
     */
    @ConditionalOnProperty(name = "deleting.enabled", matchIfMissing = true)
    @Scheduled(cron = "1 0 0 * * MON")
    public void deleteStepTable() {
        stepService.deleteStepTable();
    }

    /**
     * Registers a new step entity for the specified user.
     *
     * @param userId  The ID of the user
     * @param stepDTO A {@link StepDTO} object containing step data
     * @return A ResponseEntity containing a {@link Step} object representing the registered step,
     *         or a status 400 (BAD_REQUEST) status if the request was invalid
     */
    @Operation(summary = "Register step entity")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Step registerStep(final @PathVariable String userId,
                                             final @RequestBody @Valid StepDTO stepDTO) {
        return stepService.addSingleStepForUser(userId, stepDTO);
    }

    /**
     * Registers multiple step entities for the specified user.
     *
     * @param userId      The ID of the user
     * @param stepDtoList A list of {@link StepDTO} objects to register
     * @return A list of {@link StepDTO} objects representing the registered steps
     */
    @Operation(summary = "Register multiple step entities")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(value = "/multiple/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Step> registerMultipleSteps(final @PathVariable String userId,
                                               final @RequestBody List<@Valid StepDTO> stepDtoList) {
        return List.of(stepService.addMultipleStepsForUser(userId, stepDtoList));
    }

    /**
     * Get the latest step for a specific user
     *
     * @param userId The ID of the user whose latest step is being retrieved
     * @return A ResponseEntity containing a {@link Step} object in the body,
     * or a status 204 (NO_CONTENT) if no step data is found for the authenticated user
     */
    @Operation(summary = "Get user's latest step")
    @OkGetRequest(schemaImplementation = Step.class)
    @GetMapping(value = "/latest/{userId}")
    public Step getUsersLatestStep(final @PathVariable String userId) {
        return stepService.getLatestStepByStartTimeFromUser(userId);
    }

    @Operation(summary = "Get a user's step count per month by user and year and month)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/month/{month}"})
    public Integer getUserMonthStepCountForYearAndMonth(final @PathVariable String userId,
                                                        final @PathVariable int year,
                                                        final @PathVariable int month) {
        return stepService.getStepCountForUserYearAndMonth(userId, year, month);
    }

    @Operation(summary = "Get a user's step count per week by user and year and week)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/week/{week}"})
    public Integer getUserWeekStepCountForWeekAndYear(final @PathVariable String userId,
                                                      final @PathVariable int year,
                                                      final @PathVariable int week) {
        return stepService.getStepCountForUserYearAndWeek(userId, year, week);
    }

    @Operation(summary = "Get all month-step objects for user from year")
    @OkGetRequest(schemaImplementation = MonthStep.class)
    @GetMapping(value = "/monthsteps/user/{userId}/year/{year}")
    public List<MonthStep> getAllMonthStepsFromYearForUser(final @PathVariable String userId,
                                                           final @PathVariable int year) {
        return stepService.getMonthStepsFromYearForUser(userId, year);
    }

    @Operation(summary = "Get all week-steps for user from year")
    @OkGetRequest(schemaImplementation = WeekStep.class)
    @GetMapping(value = "/weeksteps/user/{userId}/year/{year}")
    public List<WeekStep> getAllWeeksStepsFromYearForUser(final @PathVariable String userId,
                                                          final @PathVariable int year) {
        return stepService.getWeekStepsForUserAndYear(userId, year);
    }


    /**
     * Get daily step count for current week from user
     *
     * @param userId The ID of the user
     * @return A {@link DailyWeekStepDTO} object
     */
    @Operation(summary = "Get stepCount per day for current week for a specific user")
    @OkGetRequest(schemaImplementation = DailyWeekStepDTO.class)
    @GetMapping(value = "/stepcount/{userId}/currentweekdaily")
    public DailyWeekStepDTO getStepCountByDayForUserCurrentWeek(final @PathVariable String userId) {
        return stepService.getStepsPerDayForWeek(userId);
    }

    @Operation(summary = "Get stepCount per week")
    @OkGetRequest(schemaImplementation = WeeklyStepDTO.class)
    @GetMapping(value = "/stepcount/user/{userId}/year/{year}/weekly")
    public WeeklyStepDTO getStepCountForUserPerWeek(final @PathVariable String userId,
                                                    final @PathVariable int year) {
        return stepService.getStepCountPerWeekForUser(userId, year);
    }
}

