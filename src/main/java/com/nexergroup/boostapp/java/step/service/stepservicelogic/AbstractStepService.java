package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.builder.StepDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDateDTO;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
     *
     * @param stepRepository instance of {@link StepRepository}
     * @param monthStepRepository instance of {@link MonthStepRepository}.
     * @param weekStepRepository instance of {@link WeekStepRepository}
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
     *
     * @see StepRepository#deleteAllFromStep()
     */
    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }

    /**
     * This method first checks if {@link StepDTO} object passed as input passes the {@link StepValidator#stepDataIsValid(StepDTO)} method.
     * If it does, it either updates the last {@link Step} object belonging to the same userId, or creates a new {@link Step} object
     * depending on the values of its time-fields.
     *
     * @param userId the ID of the user
     * @param stepData the {@link StepDTO} object holding the data to be added to the database
     * @return an Optional containing the data of the most recent {@link Step} object belonging to the user,
     * or a default {@link StepDTO} object indicating that the provided data is invalid
     *
     * @see StepValidator
     */
    public Step addSingleStepForUser(String userId, StepDTO stepData) {
        if (userId == null || !stepValidator.stepDataIsValid(stepData))
            throw new ValidationFailedException("userId null. Validation for step failed");
        else if (stepValidator.stepShouldBeUpdatedWithNewData(stepData))
            return updateAndSaveStep(getLatestStepFromUser(userId), stepData);
        else {
            return saveToAllTables(stepData);
        }
    }

    /**
     * This method checks if the list passed to it passes the {@link StepValidator#stepDataIsValid(List)} method,
     * and if they do, gathers the data from the list and converts it to a single {@link StepDTO} object.
     * Then the new {@link StepDTO} object gets passed to the {@link AbstractStepService#addSingleStepForUser(String, StepDTO)} method,
     * for the data to be persisted to the database.
     *
     * @param userId the ID of the user
     * @param stepDtoList the list of {@link StepDTO} objects holding the data to be persisted to the database
     * @return a {@link StepDTO} object holding the collected data from the objects in the stepDtoList,
     * or default {@link StepDTO} object indicating that the provided data is invalid
     *
     * @see StepValidator
     */
    public Step addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        if (userId == null || !stepValidator.stepDataIsValid(stepDtoList)) {
            throw new ValidationFailedException("UserId is null. Validation for step failed");
        }
        else {
            var gatheredData = gatherStepDataForUser(stepDtoList, userId);
            return addSingleStepForUser(userId, gatheredData);
        }
    }

    /**
     * This method takes in a list of {@link StepDTO} and a userId and returns a new {@link StepDTO} object with the
     * aggregated step data for that user. The method sorts the input list of {@link StepDTO} objects by end time,
     * calculates the total step count, and sets the start-, end-, and upload-times of the returned {@link StepDTO} object
     * to the earliest, latest, and latest start times in the input list respectively.
     *
     * @param stepDTOList a list containing {@link StepDTO} objects a particular user
     * @param userId the user id of the user
     * @return a {@link StepDTO} object containing the users aggregated step data
     *
     * @see StepDTOBuilder
     */
    private StepDTO gatherStepDataForUser(List<StepDTO> stepDTOList, String userId) {
        var sortedList =sortDTOListByEndTime(stepDTOList);
        var totalStepCount = sortedList.stream()
                .mapToInt(StepDTO::getStepCount)
                .sum();
        var startTime = sortedList.get(0).getStartTime();
        var endTime = sortedList.get(sortedList.size() - 1 ).getEndTime();
        var uploadTime = sortedList.get(sortedList.size() -1 ).getUploadTime();
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
     * @param stepDtoList a list of {@link StepDTO} objects to sort
     * @return a sorted list of {@link StepDTO} objects
     */
    public List<StepDTO> sortDTOListByEndTime(List<StepDTO> stepDtoList) {
        stepDtoList.sort(Comparator.comparing(StepDTO::getEndTime));
        return stepDtoList;
    }

    /**
     * Retrieve the latest {@link Step} from a specified user wrapped in an Optional object.
     *
     * @param userId the ID of the user
     * @return an Optional containing the latest {@link Step} object of the user, if found, otherwise empty Optional
     *
     * @see StepRepository#findFirstByUserIdOrderByEndTimeDesc(String)
     */
    public Step getLatestStepFromUser(String userId) {
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId).orElseThrow();
    }

    /**
     * This method retrieves the stepCount for a given user, year and month.
     *
     * @param userId the ID of the user to retrieve stepCount for
     * @param year the year to retrieve step count for
     * @param month the month to retrieve step count for
     * @return an Optional containing the stepCount for the user in the specified year and month,
     * or an empty Optional if no data found
     *
     * @see MonthStepRepository#getStepCountByUserIdYearAndMonth(String, int, int)
     */
    public Integer getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month).orElseThrow();
    }

    /**
     * This method retrieves the stepCount for a given user, year and month.
     *
     * @param userId the ID of the user to retrieve stepCount for
     * @param year the year to retrieve step count for
     * @param week the week to retrieve step count for
     * @return an Optional containing the stepCount for the user in the specified year and week,
     * or an empty Optional if no data found
     *
     * @see WeekStepRepository#getStepCountByUserIdYearAndWeek(String, int, int)
     */
    public Integer getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week).orElseThrow();
    }

    /**
     * Filters a list of user IDs, finds the matching ones and creates a list of {@link BulkStepDateDTO} objects for each user
     *
     * @param users a list of user IDs
     * @param startDate the start date in string format (yyyy-MM-dd)
     * @param endDate the end date in string format (yyyy-MM-dd)
     * @return an Optional object containing a list of {@link BulkStepDateDTO} objects
     *
     * @see StringToTimeStampConverter#convert(String)
     * @see StringComparator#getMatching(List, List)
     * @see StepRepository#getListOfAllDistinctUserId()
     */
    public List<BulkStepDateDTO> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        var stringConverter = new StringToTimeStampConverter();
        var matchingUsers = StringComparator.getMatching(users, stepRepository.getListOfAllDistinctUserId());
        var listOfBulkStepDateDto = matchingUsers.stream()
                .map(user -> createBulkStepDateDtoForUser(user, stringConverter.convert(startDate), stringConverter.convert(endDate)))
                .collect(Collectors.toList());
        return listOfBulkStepDateDto;
    }

    /**
     * Creates a {@link BulkStepDateDTO} for the given user and current week's step data.
     *
     * @param userId the id of the user for whom the step data is retrieved
     * @return an Optional {@link BulkStepDateDTO} object containing the step data for the given user and current week
     *
     * @see DateHelper#getWeek(LocalDateTime)
     * @see StepRepository#getStepDataByUserIdAndDateRange(String, Timestamp, Timestamp)
     */
    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUserForCurrentWeek(String userId) {
        var weekStart = DateHelper.getWeekStart(LocalDateTime.now(), ZoneId.systemDefault());
        var weekEnd = DateHelper.getWeekEnd(LocalDateTime.now(), ZoneId.systemDefault());
        var listOfUserStepDateDto = stepRepository.getStepDataByUserIdAndDateRange(
                userId, Timestamp.valueOf(weekStart.toLocalDateTime()), Timestamp.valueOf(weekEnd.toLocalDateTime()));
        return Optional.of(new BulkStepDateDTO(userId, listOfUserStepDateDto));
    }

    /**
     * Creates a {@link BulkStepDateDTO} for the given user and the step data within the specified date range.
     *
     * @param userId the id of the user for whom the step data is retrieved
     * @param startTime the starting time for the date range
     * @param endTime the ending time for the date range
     * @return an Optional {@link BulkStepDateDTO} object containing the step data for the given user within the specified date range
     *
     * @see StepRepository#getStepDataByUserIdAndDateRange(String, Timestamp, Timestamp)
     */
    public BulkStepDateDTO createBulkStepDateDtoForUser(String userId, Timestamp startTime, Timestamp endTime) {
        List<StepDateDTO> listOfStepsDateDtoForUser;
        try {
            listOfStepsDateDtoForUser = stepRepository.getStepDataByUserIdAndDateRange(userId, startTime, endTime);
        } catch (Exception e) {
            throw new NotFoundException();
        }
        var bulkStepDateDTO = new BulkStepDateDTO();
        listOfStepsDateDtoForUser.forEach(stepDateDTO -> bulkStepDateDTO.getStepList().add(stepDateDTO));
        return bulkStepDateDTO;
    }

    /**
     * Updates the given {@link Step} object with the stepCount, endTime and uploadTime from the {@link StepDTO} object.
     * Saves the updated {@link Step} object and creates new {@link WeekStep} and {@link MonthStep} objects if necessary.
     *
     * @param step the {@link Step} object to be updated
     * @param stepData the {@link StepDTO} object containing the data to update the {@link Step} object with
     * @return a {@link StepDTO} object holding the data of the latest {@link Step} object for the user
     */
    private Step updateAndSaveStep(Step step, StepDTO stepData) {
        stepRepository.incrementStepCountAndUpdateTimes(step, stepData.getStepCount(), stepData.getEndTime(), stepData.getUploadTime());
        updateOrSaveNewWeekStep(stepData);
        updateOrSaveNewMonthStep(stepData);
        return getLatestStepFromUser(step.getUserId());
    }


    /**
     * Updates or saves a new {@link WeekStep} object in the database,
     * if a {@link WeekStep} with the corresponding user ID, year, and week as the {@link StepDTO} passed as input.
     * If it does not exist, a new {@link WeekStep} object is saved.
     *
     * @param stepDto the {@link StepDTO} object containing the step data
     *
     * @see DateHelper#getWeek(LocalDateTime)
     * @see WeekStepRepository#findByUserIdAndYearAndWeek(String, int, int)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     */
    private void updateOrSaveNewWeekStep(StepDTO stepDto) {
        var week = DateHelper.getWeek(stepDto.getEndTime());
            weekStepRepository.findByUserIdAndYearAndWeek(stepDto.getUserId(), stepDto.getEndTime().getYear(), week)
                    .ifPresentOrElse(weekStep -> weekStepRepository.incrementWeekStepCount(weekStep.getId(), stepDto.getStepCount()),
                            () -> weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDto)));
    }

    /**
     * Updates or saves a new {@link MonthStep} object in the database,
     * if a {@link MonthStep} with the corresponding user ID, year, and month as the {@link StepDTO} passed as input.
     * If it does not exist, a new {@link MonthStep} object is saved.
     *
     * @param stepDto the {@link StepDTO} object containing the step data
     *
     * @see MonthStepRepository#findByUserIdAndYearAndMonth(String, int, int)
     * @see StepMapper#stepDtoToMonthStep(StepDTO)
     */
    private void updateOrSaveNewMonthStep(StepDTO stepDto) {
        monthStepRepository.findByUserIdAndYearAndMonth(stepDto.getUserId(), stepDto.getYear(), stepDto.getMonth())
                    .ifPresentOrElse(monthStep -> monthStepRepository.incrementMonthStepCount(monthStep.getId(), stepDto.getStepCount()),
                            () -> monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto)));
    }

    /**
     * Saves a {@link StepDTO} object to all relevant tables in the database.
     *
     * @param stepDto the {@link StepDTO} object to save
     * @return the saved {@link StepDTO} object
     *
     * @see StepMapper#stepDtoToStep(StepDTO)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     */
    private Step saveToAllTables(StepDTO stepDto) {
        stepRepository.save(StepMapper.mapper.stepDtoToStep(stepDto));
        weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDto));
        monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto));
        return StepMapper.mapper.stepDtoToStep(stepDto);
    }

    /**
     * This method creates a default {@link Step} object indicating something went wrong
     *
     * @return a {@link Step} object with userId 'Invalid Data' and uploadTime of current moment
     */
    private Step getInvalidDataObject() {
        return new Step("Invalid Data", 0, LocalDateTime.now());
        // stepCount 0 is a placeholder, throw exception in getInvalidDataObject instead ?
    }
}
