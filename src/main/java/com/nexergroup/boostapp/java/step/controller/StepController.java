package com.nexergroup.boostapp.java.step.controller;

import com.nexergroup.boostapp.java.step.controller.apiresponse.*;
import com.nexergroup.boostapp.java.step.dto.stepdto.DailyWeekStepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.WeeklyStepDTO;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.service.StepServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for handling steps taken by users
 * This class is used for production and has a security token for authentication. It is a version of {@link StepControllerDev}
 * with the added security features.
 *
 * @see StepServiceImpl
 * @see JwtValidator
 * @see GroupedApiResponse
 */
@RestController
@Profile("prod")
@Validated
@RequestMapping("/steps")
public class StepController {

    private final StepServiceImpl stepServiceImpl;

    /**
     * Constructs a new StepController with the given step service and JWT parser {@link JwtValidator}.
     *
     * @param stepServiceImpl the service to use for handling steps {@link StepServiceImpl}
     */
    public StepController(final StepServiceImpl stepServiceImpl) {
        this.stepServiceImpl = stepServiceImpl;
    }

    /**
     * Scheduled method that deletes the step table on a weekly basis (Monday at midnight).
     * This method is enabled if the property "deleting.enabled" is set to true, or if it is not set at all.
     */
    @ConditionalOnProperty(name = "deleting.enabled", matchIfMissing = true)
    @SuppressWarnings("null")
    // 1=seconds , 0=minutes, 0=hours, *-dayOfTheMonth *-month MON-Monday
    @Scheduled(cron = "1 0 0 * * MON")
    public void deleteStepTable() {
        stepServiceImpl.deleteStepTable();
    }

    /**
     * Handles a request to register a new Step entity for the specified user.
     *
     * @param jwt     A JSON Web Token (JWT) representing the authenticated user
     * @param stepDTO A {@link StepDTO} object containing step data
     * @return A ResponseEntity containing a {@link Step} object representing the registered step,
     *         or a status 400 (BAD_REQUEST) status if the request was invalid
     */
    @Operation(summary = "Register step entity")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Step registerStep(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                             final @RequestBody @Valid StepDTO stepDTO) {
        return stepServiceImpl.addSingleStepForUser(JwtValidator.getUserId(jwt), stepDTO);
    }

    /**
     * Handles a request to register multiple step entities for the specified user.
     *
     * @param jwt         A JSON Web Token (JWT) representing the authenticated user
     * @param stepDtoList A list of {@link StepDTO} objects to register
     * @return A list of {@link StepDTO} objects representing the registered steps
     */
    @Operation(summary = "Register multiple step entities")
    @OkPostResponse(schemaImplementation = Step.class)
    @PostMapping(value = "/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Step> registerMultipleSteps(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                               final @RequestBody List<@Valid StepDTO> stepDtoList) {
        return List.of(stepServiceImpl.addMultipleStepsForUser(JwtValidator.getUserId(jwt), stepDtoList));
    }

    /**
     * Get the latest step for the authenticated user.
     *
     * @param jwt A JSON Web Token (JWT) representing the authenticated user
     * @return An Optional containing a {@link StepDTO} object in the body,
     */
    @Operation(summary = "Get user's latest step")
    @OkGetRequest(schemaImplementation = Step.class)
    @GetMapping(value = "/latest")
    public Step getUsersLatestStep ( final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt) {
        return stepServiceImpl.getLatestStepByStartTimeFromUser(JwtValidator.getUserId(jwt));
    }

    /**
     * Get the step count for a specific month for an authenticated user.
     *
     * @param jwt   A JSON Web Token (JWT) representing the authenticated user
     * @param year   The year for which the step count is being retrieved
     * @param month  The month(1-12) for which the step count is being retrieved
     * @return A ResponseEntity containing the user's step count for the specified month,
     *         or a status 204 (NO_CONTENT) status if the step data is not available.
     */
    @Operation(summary = "Get a user's step count per month by user and year and month)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/year/{year}/month/{month}"})
    public Integer getUserMonthStepCountForYearAndMonth(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                                                        final @PathVariable int year,
                                                                        final @PathVariable int month) {
        return stepServiceImpl.getStepCountForUserYearAndMonth(JwtValidator.getUserId(jwt), year, month);
    }

    /**
     * Get a user's step count for a specific week of a specific year.
     *
     * @param jwt  A JSON Web Token (JWT) representing the authenticated user
     * @param year The year for which to retrieve the step count.
     * @param week The week (within the year) for which to retrieve the step count.
     * @return A ResponseEntity containing the user's step count for the specified week,
     *         or a status 204 (NO_CONTENT) status if the step count is not available.
     */
    @Operation(summary = "Get a user's step count per week by user and year and week)")
    @OkGetRequest(schemaImplementation = Integer.class)
    @GetMapping(value = {"/stepcount/year/{year}/week/{week}"})
    public Integer getUserWeekStepCountForWeekAndYear(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                                                      final @PathVariable int year,
                                                                      final @PathVariable int week) {
        return stepServiceImpl.getStepCountForUserYearAndWeek(JwtValidator.getUserId(jwt), year, week);
    }

    /**
     * Get all {@link WeekStep} objects belonging to the user for the specified year.
     *
     * @param userId The ID of the user
     * @param year The year for which to retrieve the data
     * @return A list of {@link WeekStep} objects
     */
    @Operation(summary = "Get all week-steps for user from year")
    @OkGetRequest(schemaImplementation = WeekStep.class)
    @GetMapping(value = "/weeksteps/user/{userId}/year/{year}")
    public List<WeekStep> getAllWeeksStepsFromYearForUser(final @PathVariable String userId,
                                                          final @PathVariable int year) {
        return stepServiceImpl.getWeekStepsForUserAndYear(userId, year);
    }

    /**
     * Get all {@link MonthStep} objects belonging to the user for the specified year.
     *
     * @param userId The ID of the user
     * @param year The year for which to retrieve the data
     * @return A list of {@link MonthStep} objects
     */
    @Operation(summary = "Get all month-step objects for user from year")
    @OkGetRequest(schemaImplementation = MonthStep.class)
    @GetMapping(value = "/monthsteps/user/{userId}/year/{year}")
    public List<MonthStep> getAllMonthStepsFromYearForUser(final @PathVariable String userId,
                                                           final @PathVariable int year) {
        return stepServiceImpl.getMonthStepsFromYearForUser(userId, year);
    }

    /**
     * Get daily step count for current week from user
     *
     * @param jwt A JSON Web Token (JWT) representing the authenticated user
     * @return A {@link DailyWeekStepDTO} object
     */
    @Operation(summary = "Get stepCount per day for current week for a specific user")
    @OkGetRequest(schemaImplementation = DailyWeekStepDTO.class)
    @GetMapping(value = "/stepcount/currentweekdaily")
    public DailyWeekStepDTO getStepCountByDayForUserCurrentWeek(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt) {
        return stepServiceImpl.getStepsPerDayForWeek(JwtValidator.getUserId(jwt));
    }

    /**
     * @param userId The ID of the user
     * @param year The year from which to retrieve the data
     * @return A {@link WeeklyStepDTO} object
     */
    @Operation(summary = "Get stepCount per week")
    @OkGetRequest(schemaImplementation = WeeklyStepDTO.class)
    @GetMapping(value = "/stepcount/user/{userId}/year/{year}/weekly")
    public WeeklyStepDTO getStepCountForUserPerWeek(final @PathVariable String userId,
                                                    final @PathVariable int year) {
        return stepServiceImpl.getStepCountPerWeekForUser(userId, year);
    }
}
