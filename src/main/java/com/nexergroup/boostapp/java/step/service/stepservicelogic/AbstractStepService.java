package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.builder.StepDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.DailyWeekStepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.WeeklyStepDTO;
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
import com.nexergroup.boostapp.java.step.validator.boostappvalidator.StepValidator;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

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
        // Otherwise new Step objects are created and saved/updated to each table in database
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
    public Optional<Step> getLatestStepFromUser(String userId) {
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
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
        return monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month)
                .orElse(0);
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
        return weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week)
                .orElse(0);
    }





    /**
     * Converts a StepDTO object to Step, WeekStep, and MonthStep objects and saves them in the database
     *
     * @param stepDTO the {@link StepDTO} object to convert, holding the new data
     * @return the users most recently stored {@link Step} object
     */
    private Step  saveToAllTables(StepDTO stepDTO) {
        try {
            var latestStepInDB = getLatestStepFromUser(stepDTO.getUserId())
                    .orElse(new Step());
            if (stepValidator.shouldUpdateStep(stepDTO))
                // make method use id instead of object
                stepRepository.setTotalStepCountAndUpdateDateTime(latestStepInDB, latestStepInDB.getStepCount() + stepDTO.getStepCount(), stepDTO.getEndTime(), stepDTO.getUploadTime());
            else {
                stepRepository.save(StepMapper.mapper.stepDtoToStep(stepDTO));
            }
        } catch (RuntimeException runtimeException) {
            throw new RuntimeException("1 " + runtimeException.getMessage());
        }
            try {
                // Get week and month value of new step
                var weekOfNewStep = DateHelper.getWeek(stepDTO.getStartTime());
                var monthOfNewStep = stepDTO.getStartTime().getMonthValue();

                //
                var latestWeekStep = weekStepRepository.findTopByUserIdOrderByIdDesc(stepDTO.getUserId())
                        .orElse(new WeekStep());
                var latestMonthStep = monthStepRepository.findTopByUserIdOrderByIdDesc(stepDTO.getUserId())
                        .orElse(new MonthStep());


                // If new data is from same week as existing week step in db update step count, otherwise save new object
                if (weekOfNewStep == latestWeekStep.getWeek())
                    weekStepRepository.setTotalStepCountById(latestWeekStep.getId(), stepDTO.getStepCount() + latestWeekStep.getStepCount());
                else
                    weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDTO));

                // Repeat previous procedure for Month Step
                if (monthOfNewStep == latestMonthStep.getMonth())
                    monthStepRepository.setTotalStepCountById(latestMonthStep.getId(), stepDTO.getStepCount() + latestMonthStep.getStepCount());
                else
                    monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDTO));
                // If
            } catch (NullPointerException nullPointerException) {
                throw new NullPointerException("NullpointerExceptopn when saving " + nullPointerException.getMessage());
            } catch (RuntimeException runtimesException) {
                throw new RuntimeException("Java API failed to persist new step data to database " + runtimesException.getMessage());
            }

            return getLatestStepFromUser(stepDTO.getUserId())
                    .orElseThrow(() -> new NotFoundException("Unknown error in Java save function. Step was not persisted to database"));

    }

    /**
     * Retrieves stepCount per day for a specified user the current week.
     *
     * @param userId the id of a user
     * @return a {@link DailyWeekStepDTO} object containing the daily stepCount
     */
    public DailyWeekStepDTO getStepsPerDayForWeek(String userId) {
        if (userId == null)
            throw new ValidationFailedException("User id and time must not be null");
        // Retrieve all Step objects belonging to the user from the step table
        var stepsFromDatabase = stepRepository.getListOfStepsByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No steps found in database for user id: " + userId));
        // Create an ArrayList with 7 slots and the value '0' added to each slot
        var stepCountsByDay = getDefaultWeekList();

        // Loop through the retrieved Step objects and add the stepCount to the correct index of the list
        for (Step step : stepsFromDatabase) {
            // Sets the index the Step object is connected to depending on the startTime of the Step
            var index = step.getStartTime().getDayOfWeek().getValue() - 1;
            // Adds the stepCount of the Step object to the index
            stepCountsByDay.set(index, stepCountsByDay.get(index) + step.getStepCount());
        }
        return new DailyWeekStepDTO(userId, DateHelper.getWeek(ZonedDateTime.now()), stepCountsByDay);
    }

    /**
     * Creates a List with 7 slots to store integers with '0' as a default value
     *
     * @return An integer ArrayList with capacity of 7 slots with the value '0' in each
     */
    private static List<Integer> getDefaultWeekList() {
        return  new ArrayList<>(Collections.nCopies(7, 0));
    }

    public WeeklyStepDTO getStepCountPerWeekForUser(String userId) {

        // Get current date time and week number
        var now = ZonedDateTime.now();
        var currentWeek = DateHelper.getWeek(now);

        // Create a list with one slot per week of the year
        var weekList = new ArrayList<Integer>(52);

        // Fill the list with the stepCount of every week until current week
        for (int i = 0; i < currentWeek - 1; i++) {
            var stepCountWeek = weekStepRepository.getStepCountByUserIdYearAndWeek(userId, now.getYear(), i)
                    .orElse(0);
            weekList.add(stepCountWeek);
        }

        // Fill in the rest with '0'
        for (int i = currentWeek - 1; i < 52; i++) {
            weekList.add(0);
        }

        return new WeeklyStepDTO(userId, weekList);
    }
}
