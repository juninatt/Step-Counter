package com.nexergroup.boostapp.java.step.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.nexergroup.boostapp.java.step.controller.apiresponse.GroupedApiResponse;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.exception.NotFoundException;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.service.StepService;

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
public class StepControllerDev {

    private final StepService stepService;


    /**
     * @param stepService Service class containing business logic {@link StepService}
     */
    public StepControllerDev(StepService stepService) {
        this.stepService = stepService;
    }

    /**
     * Deletes all step-table data from the database. This method is intended for development purposes only and should only be run
     * when the property "deleting.enabled" is set. The method is scheduled to run every Monday at midnight.
     */
    @ConditionalOnProperty(name = "deleting.enabled", matchIfMissing = true)
    @SuppressWarnings("null")
    //1=secund , 0=minut, 0= hours, *-dayOfTheMonth *-month MON-Monday
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
    @GroupedApiResponse
    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Step> registerStep(final @PathVariable String userId,
                                             final @RequestBody @Valid StepDTO stepDTO) {
        return stepService.addSingleStepForUser(userId, stepDTO)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    /**
     * Registers multiple step entities for the specified user.
     *
     * @param userId      The ID of the user
     * @param stepDtoList A list of {@link StepDTO} objects to register
     * @return A list of {@link StepDTO} objects representing the registered steps
     */
    @Operation(summary = "Register multiple step entities")
    @GroupedApiResponse
    @PostMapping(value = "/multiple/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<StepDTO> registerMultipleSteps(final @PathVariable String userId,
                                               final @RequestBody List<@Valid StepDTO> stepDtoList) {
        return stepService.addMultipleStepsForUser(userId, stepDtoList);
    }


    /**
     * Get step count per day for a list of users by start date and end date
     *
     * @param users     List of userIds
     * @param startDate Start date as String in the format "yyyy-[m]m-[d]d"
     * @param endDate   End date as String in the format "yyyy-[m]m-[d]d" (optional)
     * @return A list of {@link BulkStepDateDTO} objects
     * @throws NotFoundException if no step data is found for the specified users and date range
     */
    @Operation(summary = "Get step count per day for a list of users by start date and end date (optional).")
    @GroupedApiResponse
    @PostMapping(value = {"/stepcount/bulk/date"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BulkStepDateDTO> getBulkStepsByUsers(final @RequestBody List<String> users,
                                                     final @RequestParam String startDate,
                                                     final @RequestParam(required = false) String endDate) {
        return stepService.filterUsersAndCreateListOfBulkStepDateDtoWithRange(users, startDate, endDate)
                .orElseThrow(NotFoundException::new);
    }


    /**
     * Get the latest step for a specific user
     *
     * @param userId The ID of the user whose latest step is being retrieved
     * @return A ResponseEntity containing a {@link Step} object in the body,
     * or a status 204 (NO_CONTENT) if no step data is found for the authenticated user
     */
    @Operation(summary = "Get user's latest step")
    @GroupedApiResponse
    @GetMapping(value = "/latest/{userId}")
    public ResponseEntity<Step> getUsersLatestStep(final @PathVariable String userId) {
        return stepService.getLatestStepFromUser(userId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    /**
     * Get the step count per month for a specific user by user ID and year and month
     *
     * @param userId The ID of the user whose step count is being retrieved
     * @param year   The year for which the step count is being retrieved
     * @param month  The month for which the step count is being retrieved
     * @return A ResponseEntity containing the user's step count for the specified month,
     *         or a status 204 (NO_CONTENT) status if the step data is not available.
     */
    @Operation(summary = "Get a user's step count per month by user ID and year and month)")
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/month/{month}"})
    public ResponseEntity<Integer> getUserMonthSteps(final @PathVariable String userId,
                                                     final @PathVariable int year,
                                                     final @PathVariable int month) {
        return stepService.getStepCountForUserYearAndMonth(userId, year, month)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    /**
     * Get the step count per week for a specific user by user ID and year and week
     *
     * @param userId The ID of the user whose step count is being retrieved
     * @param year   The year for which the step count is being retrieved
     * @param week   The week for (within the year) which the step count is being retrieved
     * @return A ResponseEntity containing the user's step count for the specified week,
     *         or a status 204 (NO_CONTENT) status if the step count is not available.
     */
    @Operation(summary = "Get a user's step count per week by user ID and year and week)")
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/{userId}/year/{year}/week/{week}"})
    public ResponseEntity<Integer> getUserWeekStepCountForWeekAndYear(final @PathVariable String userId,
                                                                      final @PathVariable int year,
                                                                      final @PathVariable int week) {
        return stepService.getStepCountForUserYearAndWeek(userId, year, week)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    /**
     * Retrieves a list of steps per day for the current week for a specific user.
     *
     * @param userId the ID of the user to retrieve step data for
     * @return A ResponseEntity containing a list of {@link StepDateDTO} objects representing
     *         the steps taken by the user on each day of the current week,
     *         or a status 204 (NO_CONTENT) status if the step data is not available.
     */
    @Operation(summary = "Get list of steps per day per current week)")
    @GroupedApiResponse
    @GetMapping(value = {"/stepcount/{userId}/currentweek"})
    public ResponseEntity<BulkStepDateDTO> getUserWeekSteps(final @PathVariable String userId) {
        return stepService.createBulkStepDateDtoForUserForCurrentWeek(userId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}

