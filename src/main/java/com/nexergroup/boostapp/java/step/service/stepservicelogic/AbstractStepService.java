package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.builder.StepDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.DailyWeekStepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.WeeklyStepDTO;
import com.nexergroup.boostapp.java.step.exception.NotFoundException;
import com.nexergroup.boostapp.java.step.exception.ValidationFailedException;
import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.mapper.StepMapper;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.validator.boostappvalidator.StepValidator;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
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
    public Step getLatestStepByStartTimeFromUser(String userId) {
        var now = ZonedDateTime.now(ZoneId.systemDefault());
        return stepRepository.findFirstByUserIdOrderByStartTimeDesc(userId)
                .orElse(new Step(userId, 0, now, now.plusSeconds(1), now.plusSeconds(2)));
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
     * @return The updated {@link Step} object
     */
    private Step  saveToAllTables(StepDTO stepDTO) {
        Step updatedStep;
        try {
            // If new data is from same day as latest step object, the step is updated
            if (stepValidator.shouldUpdateStep(stepDTO)) {
                updatedStep = updateStep(stepDTO);
            }
            else {
                // If not, new step object is created and the data persisted to all tables
                updatedStep = stepRepository.save(StepMapper.mapper.stepDtoToStep(stepDTO));
                addStepDataToWeekStepTable(stepDTO, stepDTO.getStepCount());
                addStepDataToMonthStepTable(stepDTO, stepDTO.getStepCount());
            }
            } catch (NullPointerException nullPointerException) {
                throw new NullPointerException("NullPointerException when saving " + nullPointerException.getMessage());
            } catch (RuntimeException runtimesException) {
                throw new RuntimeException("Java API failed to persist new step data to database " + runtimesException.getMessage());
            }

        // If persisted step-count is 0, something went wrong and RuntimeException is thrown, otherwise the new/updated step is returned to the caller
        if (updatedStep.getStepCount() > 0)
            return updatedStep;
        else
            throw new NotFoundException("Unknown error in Java save function. Step was not persisted to database");
    }

    private Step updateStep(StepDTO stepDTO) {
        Step updatedStep;
        // Fetch users latest step object from database
        var latestStepInDB = getLatestStepByStartTimeFromUser(stepDTO.getUserId());
        // Get the value to be added to the current step object(The DTO-stepCount is always the new total of the day)
        var stepCountIncrease = Math.abs(latestStepInDB.getStepCount() - stepDTO.getStepCount());
        // Add stepCount to all tables without creating new objects in database
        updatedStep = updateStep(latestStepInDB, stepDTO);
        updateWeekStepForUser(stepDTO.getUserId(), stepCountIncrease);
        updateMonthStep(stepDTO.getUserId(), stepCountIncrease);
        return updatedStep;
    }

    private void addStepDataToMonthStepTable(StepDTO stepDTO, int newStepCount) {
        // Fetch month-step object from same month and year from database
        var monthStepToUpdate = monthStepRepository.findByUserIdAndYearAndMonth(stepDTO.getUserId(), stepDTO.getStartTime().getYear(), stepDTO.getStartTime().getMonthValue());
        // If a month-step object from the same month was found it is updated
        monthStepToUpdate.ifPresentOrElse(monthStep -> {
                    monthStep.setStepCount(monthStep.getStepCount() + newStepCount);
                    monthStepRepository.save(monthStep);
                },
                // If no month-step from same month and year was found a new object is created and stored in database
                () -> monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDTO)));
    }

    private void addStepDataToWeekStepTable(StepDTO stepDTO, int newStepCount) {
        // Fetch week-step object from same week and year from database
        var weekStepToUpdate = weekStepRepository.findByUserIdAndYearAndWeek(stepDTO.getUserId(), stepDTO.getStartTime().getYear(), DateHelper.getWeek(stepDTO.getStartTime()));
        // If a week-step object from the same week was found it is updated
        weekStepToUpdate.ifPresentOrElse(weekStep -> {
                    weekStep.setStepCount(weekStep.getStepCount() + newStepCount);
                    weekStepRepository.save(weekStep);
                },
                // If no week-step from same week and year was found a new object is created and stored in database
                () -> weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDTO)));
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

    /**
     * Retrieves all {@link WeekStep} objects belonging to the user and adds step count values to new list which is returned.
     *
     * @param userId The ID of the user
     * @return A {@link WeeklyStepDTO} object containing weekly step count for current year
     */
    public WeeklyStepDTO getStepCountPerWeekForUser(String userId, int year) {
        // Fetch all step objects belonging to user from current year
        var fetchedWeekStepList = weekStepRepository.getAllWeekStepsFromYearForUser(year , userId);
        // Create a list with 52 slots of 0
        ArrayList<Integer> weeklyStepCountList = new ArrayList<>(Collections.nCopies(53, 0));

        // Add step count from week-step objects to corresponding slot in new array
        for (WeekStep weekStep : fetchedWeekStepList) {
            int weekNumber = weekStep.getWeek();
            int weekStepCount = weekStep.getStepCount();
            if (weekNumber >= 1 && weekNumber <= 52) {
                weeklyStepCountList.set(weekNumber, weekStepCount);
            }
        }
        return new WeeklyStepDTO(userId, weeklyStepCountList);
    }

    private Step updateStep(Step step, StepDTO stepDTO) {
        step.setStepCount(stepDTO.getStepCount());
        step.setEndTime(stepDTO.getEndTime());
        step.setUploadTime(stepDTO.getUploadTime());
        return stepRepository.save(step);
    }
    private void updateMonthStep(String userId, int stepCount) {
        var monthStep = monthStepRepository.findTopByUserIdOrderByIdDesc(userId).get();
        monthStep.setStepCount(monthStep.getStepCount() + stepCount);
        monthStepRepository.save(monthStep);
    }

    private void updateWeekStepForUser(String userId, int stepCount) {
        var weekStep = weekStepRepository.findTopByUserIdOrderByIdDesc(userId).get();
        weekStep.setStepCount(weekStep.getStepCount() + stepCount);
        weekStepRepository.save(weekStep);
    }

    public List<WeekStep> getWeekStepsForUserAndYear(String userId, int year) {
        return weekStepRepository.getAllWeekStepsFromYearForUser(year, userId);
    }
}
