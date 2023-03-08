package com.nexergroup.boostapp.java.step.controller;

import com.nexergroup.boostapp.java.step.controller.apiresponse.GroupedApiResponse;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.exception.NotFoundException;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.service.StepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for handling steps taken by users
 * This class is used for production and has a security token for authentication. It is a version of {@link StepControllerDev}
 * with the added security features.
 *
 * @see StepService
 * @see JwtValidator
 * @see GroupedApiResponse
 */
@RestController
@Profile("prod")
@Validated
@RequestMapping("/steps")
public class StepController {

    private final StepService stepService;

    /**
     * Constructs a new StepController with the given step service and JWT parser {@link JwtValidator}.
     *
     * @param stepService the service to use for handling steps {@link StepService}
     */
    public StepController(final StepService stepService) {
        this.stepService = stepService;
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
        stepService.deleteStepTable();
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
    @GroupedApiResponse
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Step registerStep(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                             final @RequestBody @Valid StepDTO stepDTO) {
        return stepService.addSingleStepForUser(JwtValidator.getUserId(jwt), stepDTO);
    }

    /**
     * Handles a request to register multiple step entities for the specified user.
     *
     * @param jwt         A JSON Web Token (JWT) representing the authenticated user
     * @param stepDtoList A list of {@link StepDTO} objects to register
     * @return A list of {@link StepDTO} objects representing the registered steps
     */
    @Operation(summary = "Register multiple step entities")
    @GroupedApiResponse
    @PostMapping(value = "/multiple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Step> registerMultipleSteps(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                               final @RequestBody List<@Valid StepDTO> stepDtoList) {
        return List.of(stepService.addMultipleStepsForUser(JwtValidator.getUserId(jwt), stepDtoList));
    }


    /**
     * Get step count per day for a list of users by start date and end date.
     *
     * @param users     List of userIds
     * @param startDate Start date as String in the format "yyyy-[m]m-[d]d"
     * @param endDate   End date as String in the format "yyyy-[m]m-[d]d" (optional)
     * @return A list of {@link BulkStepDateDTO} objects
     * @throws NotFoundException if no step data is found for the specified users and date range
     */
    @Operation(summary = "Get step count per day for a list of users by start date and end date (optional).")
    @GroupedApiResponse
    @PostMapping(value = "/stepcount/bulk/date", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BulkStepDateDTO> getBulkStepsByUsers(final @RequestBody List<String> users,
                                                     final @RequestParam String startDate,
                                                     final @RequestParam(required = false) String endDate) {
        return stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(users, startDate, endDate);
    }

    /**
     * Get the latest step for the authenticated user.
     *
     * @param jwt A JSON Web Token (JWT) representing the authenticated user
     * @return An Optional containing a {@link StepDTO} object in the body,
     */
    @Operation(summary = "Get user's latest step")
    @GroupedApiResponse
    @GetMapping(value = "/latest")
    public Step getUsersLatestStep ( final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt) {
        return stepService.getLatestStepFromUser(JwtValidator.getUserId(jwt));
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
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/year/{year}/month/{month}"})
    public Integer getUserMonthStepCountForYearAndMonth(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                                                        final @PathVariable int year,
                                                                        final @PathVariable int month) {
        return stepService.getStepCountForUserYearAndMonth(JwtValidator.getUserId(jwt), year, month);
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
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/year/{year}/week/{week}"})
    public Integer getUserWeekStepCountForWeekAndYear(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt,
                                                                      final @PathVariable int year,
                                                                      final @PathVariable int week) {
        return stepService.getStepCountForUserYearAndWeek(JwtValidator.getUserId(jwt), year, week);
    }

    /**
     * Get a list of steps per day for the current week for an authenticated user.
     *
     * @param jwt A JSON Web Token (JWT) representing the authenticated user
     * @return A ResponseEntity containing a list of {@link StepDateDTO} objects representing
     *         the steps taken by the user on each day of the current week,
     *         or a status 204 (NO_CONTENT) status if the step data is not available.
     */
    @Operation(summary = "Get list of steps per day per current week)")
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/currentweek"})
    public ResponseEntity<BulkStepDateDTO> getStepDataByUserForCurrentWeek(final @AuthenticationPrincipal @Parameter(hidden = true) Jwt jwt) {
        return stepService.createBulkStepDateDtoForUserForCurrentWeek(JwtValidator.getUserId(jwt))
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
