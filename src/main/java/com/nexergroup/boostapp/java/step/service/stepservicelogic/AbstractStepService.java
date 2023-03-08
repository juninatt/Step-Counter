package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.builder.StepDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.WeekStepDTO;
import com.nexergroup.boostapp.java.step.exception.NotFoundException;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.mapper.StepMapper;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.util.StringComparator;
import com.nexergroup.boostapp.java.step.util.parser.StringToTimeStampConverter;
import com.nexergroup.boostapp.java.step.validator.boostappvalidator.StepValidator;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AbstractStepService is a class which provides methods to manage and maintain step data for a user.
 * It contains methods to add, update, and delete step data,
 * as well as methods to retrieve the latest step data or step data for a specific date range.
 *
 * @see StepRepository
 * @see WeekStepRepository
 * @see MonthStepRepository
 */
@Component
public abstract class AbstractStepService {

    private final StepRepository stepRepository;
    private final WeekStepRepository weekStepRepository;
    private final MonthStepRepository monthStepRepository;
    private final StepValidator stepValidator;

    /**
     * Constructor for AbstractStepService class.
     */
    public AbstractStepService(final StepRepository stepRepository,
                       final MonthStepRepository monthStepRepository,
                       final WeekStepRepository weekStepRepository) {
        this.stepRepository = stepRepository;
        this.monthStepRepository = monthStepRepository;
        this.weekStepRepository = weekStepRepository;
        this.stepValidator = new StepValidator(stepRepository);
    }

    /**
     * Deletes all records from the step table.
     */
    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }

    /**
     * Adds Step-data to a specified user and stores it in the database, after checking the data for bad values.
     *
     * @param userId the ID of the user
     * @param stepDTO a {@link StepDTO} object containing the new data
     * @return the users most recently stored {@link Step} object
     */
    public Step addSingleStepForUser(String userId, StepDTO stepDTO) {
        // Checks all fields for null or bad data
        if (userId == null || !stepValidator.stepDataIsValid(stepDTO))
            throw new ValidationFailedException("userId null. Validation for Step failed");
        // If new data should be added to users most recent Step object, Step is updated
        else if (stepValidator.shouldUpdateStep(stepDTO))
            return updateAndSaveStep(getLatestStepFromUser(userId), stepDTO);
        // Otherwise new Step objects are created and saved to each table in database
        else {
            return saveToAllTables(stepDTO);
        }
    }

    /**
     * Add Step-data to a specified user in form of a list, and stores it in the database after checking for bad values
     *
     * @param userId the ID of the user
     * @param stepDTOList a list of {@link StepDTO} objects containing the new data
     * @return the users most recently stored {@link Step} object
     */
    public Step addMultipleStepsForUser(String userId, List<StepDTO> stepDTOList) {
        // Checks all fields for null or bad data
        if (userId == null || !stepValidator.stepDataIsValid(stepDTOList)) {
            throw new ValidationFailedException("Validation of new data failed");
        }
        else {
            // If valid, gathers the data into a single StepDTO object that gets added to database
            var gatheredData = gatherStepDataForUser(stepDTOList, userId);
            return addSingleStepForUser(userId, gatheredData);
        }
    }

    /**
     * This method takes in a list of {@link StepDTO} objects and a userId and returns a new {@link StepDTO} object with the
     * aggregated step data for that user.
     *
     * @param stepDTOList a list containing {@link StepDTO} objects a particular user
     * @param userId the id of the user
     * @return a {@link StepDTO} object containing the users aggregated step data
     */
    private StepDTO gatherStepDataForUser(List<StepDTO> stepDTOList, String userId) {
        // Sorts the list by endTime and collects to a list. 'Oldest' endTime at index 0
        var sortedList =sortDTOListByEndTime(stepDTOList);
        // Gets the total sum of the stepCount of the objects in the list
        var totalStepCount = sortedList.stream()
                .mapToInt(StepDTO::getStepCount)
                .sum();
        var startTime = sortedList.get(0).getStartTime();
        var endTime = sortedList.get(sortedList.size() - 1 ).getEndTime();
        var uploadTime = sortedList.get(sortedList.size() -1 ).getUploadTime();
        // Creates and returns new StepCTO with startTime of the oldest object and endTime of the newest object
        // and sum of all objects stepCount
        return new StepDTOBuilder()
                .withUserId(userId)
                .withStepCount(totalStepCount)
                .withStartTime(startTime)
                .withEndTime(endTime)
                .withUploadTime(uploadTime)
                .build();
    }

    /**
     * Sorts a list of {@link StepDTO} objects by the value of their endTime field.
     *
     * @param stepDTOList a list of {@link StepDTO} objects to sort
     * @return a sorted list of {@link StepDTO} objects
     */
    public List<StepDTO> sortDTOListByEndTime(List<StepDTO> stepDTOList) {
        stepDTOList.sort(Comparator.comparing(StepDTO::getEndTime));
        return stepDTOList;
    }

    /**
     * Retrieve the most recently stored {@link Step} object for the specified user from the database
     *
     * @param userId the ID of the user
     * @return the users most recently stored {@link Step} object
     */
    public Step getLatestStepFromUser(String userId) {
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).orElseThrow();
    }

    /**
     * Retrieve the stepCount for a given user, year and month.
     *
     * @param userId the id of the user
     * @param year the requested year
     * @param month the requested month
     * @return the users total stepCount for the requested month
     */
    public Integer getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month).orElseThrow();
    }

    /**
     * Retrieve the stepCount for a given user, year and week.
     *
     * @param userId the ID of the user
     * @param year the requested year
     * @param week the requested week
     * @return the users total stepCount for the requested week
     */
    public Integer getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week).orElseThrow();
    }

    /**
     * Filters a list of userId:s as strings, finds the matching ones and creates {@link BulkStepDateDTO} for each user
     *
     * @param users a list of userId:s
     * @param startDate the start date in string format (yyyy-MM-dd)
     * @param endDate the end date in string format (yyyy-MM-dd)
     * @return a list of {@link BulkStepDateDTO} objects
     */
    public List<BulkStepDateDTO> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        var stringConverter = new StringToTimeStampConverter();
        // Convert the string to a time format supported by the database
        var sqlStartDate = stringConverter.convert(startDate);
        var sqlEndDate = stringConverter.convert(endDate);
        // Collect the matching string to a list (removing requested users not found in database)
        var matchingUsers = StringComparator.getMatching(users, stepRepository.getListOfAllDistinctUserId());
        // Create a BulkStepDateDTO object for each user, collect them to a list and return it to the caller
        return matchingUsers.stream()
                .map(user -> createBulkStepDateDtoForUser(user, sqlStartDate, sqlEndDate))
                .collect(Collectors.toList());
    }

    /**
     * Creates a {@link BulkStepDateDTO} for the given user and current week's step data.
     *
     * @param userId the id of the user
     * @return a {@link BulkStepDateDTO} object containing the step data for the given user and current week
     */
    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUserForCurrentWeek(String userId) {
        // Get the start and end of the week as ZonedDateTime objects
        var weekStart = DateHelper.getWeekStart(LocalDateTime.now(), ZoneId.systemDefault());
        var weekEnd = DateHelper.getWeekEnd(LocalDateTime.now(), ZoneId.systemDefault());
        // Retrieve a list of StepDateDTO:s for the specified user and period
        var listOfUserStepDateDto = stepRepository.getStepDataByUserIdAndDateRange(
                userId, Timestamp.valueOf(weekStart.toLocalDateTime()), Timestamp.valueOf(weekEnd.toLocalDateTime()));
        // Create a BulkStepDateDTO object for the user from the list of StepDateDTO:s and return it to the caller
        return Optional.of(new BulkStepDateDTO(userId, listOfUserStepDateDto));
    }

    /**
     * Creates a {@link BulkStepDateDTO} for the given user and the step data within the specified date range.
     *
     * @param userId the id of the user
     * @param startTime the starting time of time period
     * @param endTime the ending time of time period
     * @return an {@link BulkStepDateDTO} object containing the step data for the given user within the specified date range
     */
    public BulkStepDateDTO createBulkStepDateDtoForUser(String userId, Timestamp startTime, Timestamp endTime) {
        List<StepDateDTO> listOfStepsDateDtoForUser;
        // Retrieve a list of StepDateDTO:s for the specified user and time period
        try {
            listOfStepsDateDtoForUser = stepRepository.getStepDataByUserIdAndDateRange(userId, startTime, endTime);
        }
        catch (Exception exception) {
            throw new NotFoundException();
        }
        // Create a BulkStepDateDTO object and add the retrieved StepDateDTO:s to it
        var bulkStepDateDTO = new BulkStepDateDTO();
        listOfStepsDateDtoForUser.forEach(stepDateDTO -> bulkStepDateDTO.getStepList().add(stepDateDTO));
        return bulkStepDateDTO;
    }

    /**
     * Updates the provided {@link Step} object in the database,
     * and creates new {@link WeekStep} and {@link MonthStep} objects if necessary based on whether a new week/month has started.
     *
     * @param step the {@link Step} object to be updated
     * @param stepDTO the {@link StepDTO} object containing the new step data
     * @return the updated {@link Step} object
     */
    private Step updateAndSaveStep(Step step, StepDTO stepDTO) {
        // Updates all relevant fields of the Step object in the database
        stepRepository.incrementStepCountAndUpdateTimes(step, stepDTO.getStepCount(), stepDTO.getEndTime(), stepDTO.getUploadTime());
        updateOrSaveNewWeekStep(stepDTO);
        updateOrSaveNewMonthStep(stepDTO);
        return getLatestStepFromUser(step.getUserId());
    }


    /**
     * Updates or saves a new {@link WeekStep} object in the database,
     *
     * @param stepDTO the {@link StepDTO} object containing the new step data
     */
    private void updateOrSaveNewWeekStep(StepDTO stepDTO) {
        // Fetch users most recent WeekStep from database
        weekStepRepository.findTopByUserIdOrderByIdDesc(stepDTO.getUserId())
                .ifPresent(weekStep -> {
                    // If the WeekStep should be updated its stepCount is increased
                    if (stepValidator.shouldUpdateWeekStep(stepDTO, weekStep))
                        weekStepRepository.incrementWeekStepCount(weekStep.getId(), stepDTO.getStepCount());
                        // Otherwise a new WeekStep is created
                    else
                        weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDTO));
        });
    }

    /**
     * Updates or saves a new {@link MonthStep} object in the database,
     *
     * @param stepDTO the {@link StepDTO} object containing the new step data
     */
    private void updateOrSaveNewMonthStep(StepDTO stepDTO) {
        // Fetch users most recent MonthStep from database
        monthStepRepository.findTopByUserIdOrderByIdDesc(stepDTO.getUserId())
                    .ifPresent(monthStep -> {
                        // If the MonthStep should be updated its stepCount is increased
                        if (stepValidator.shouldUpdateMonthStep(stepDTO, monthStep))
                            monthStepRepository.incrementMonthStepCount(monthStep.getId(), stepDTO.getStepCount());
                        else
                            // Otherwise a new MonthStep is created
                            monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDTO));
                    });
    }

    /**
     * Converts a StepDTO object to Step, WeekStep, and MonthStep objects and saves them in the database
     *
     * @param stepDTO the {@link StepDTO} object to convert, holding the new data
     * @return the users most recently stored {@link Step} object
     */
    private Step saveToAllTables(StepDTO stepDTO) {
        // Uses the StepMapper interface to convert the StepDTO to the necessary objects and saves them in the database
        var newStep = StepMapper.mapper.stepDtoToStep(stepDTO);
        stepRepository.save(newStep);
        weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDTO));
        monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDTO));
        return newStep;
    }

    /**
     * Retrieves stepCount per day for a specified user the current week.
     *
     * @param userId the id of a user
     * @return a {@link WeekStepDTO} object containing the daily stepCount
     */
    public WeekStepDTO getStepsPerDayForWeek(String userId) {
        if (userId == null)
            throw new ValidationFailedException("User id and time must not be null");
        // Retrieve all Step objects belonging to the user from the step table
        var stepsFromDatabase = stepRepository.getListOfStepsByUserId(userId)
                .orElse(List.of(new Step(userId, 0, ZonedDateTime.now())));
        // Create an ArrayList with 7 slots and the value '0' added to each slot
        var stepCountsByDay = getDefaultWeekList();

        // Loop through the retrieved Step objects and add the stepCount to the correct index of the list
        for (Step step : stepsFromDatabase) {
            // Sets the index the Step object is connected to depending on the startTime of the Step
            var index = step.getStartTime().getDayOfWeek().getValue() - 1;
            // Adds the stepCount of the Step object to the index
            stepCountsByDay.set(index, stepCountsByDay.get(index) + step.getStepCount());
        }
        return new WeekStepDTO(userId, DateHelper.getWeek(ZonedDateTime.now()), stepCountsByDay);
    }

    /**
     * Creates a List with 7 slots to store integers with '0' as a default value
     *
     * @return An integer ArrayList with capacity of 7 slots with the value '0' in each
     */
    private static List<Integer> getDefaultWeekList() {
        return  new ArrayList<>(Collections.nCopies(7, 0));
    }
}
