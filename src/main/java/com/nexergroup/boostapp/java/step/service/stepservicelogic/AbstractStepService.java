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
     * Adds Step-data to a specified user and stores it in the database, after checking the data for bad values.
     *
     * @param userId the ID of the user
     * @param stepData a {@link StepDTO} object containing the new data
     * @return the users most recently stored {@link Step} object
     */
    public Step addSingleStepForUser(String userId, StepDTO stepData) {
        // Checks all fields for null or bad data
        if (userId == null || !stepValidator.stepDataIsValid(stepData))
            throw new ValidationFailedException("userId null. Validation for Step failed");
        // If new data should be added to users most recent Step object, Step is updated
        else if (stepValidator.shouldUpdateStep(stepData))
            return updateAndSaveStep(getLatestStepFromUser(userId), stepData);
        // Otherwise new Step objects are created and saved to each table in database
        else {
            return saveToAllTables(stepData);
        }
    }

    /**
     * Add Step-data to a specified user in form of a list, and stores it in the database after checking for bad values
     *
     * @param userId the ID of the user
     * @param stepDtoList a list of {@link StepDTO} objects containing the new data
     * @return the users most recently stored {@link Step} object
     */
    public Step addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        // Checks all fields for null or bad data
        if (userId == null || !stepValidator.stepDataIsValid(stepDtoList)) {
            throw new ValidationFailedException("Validation of new data failed");
        }
        else {
            // If valid, gathers the data into a single StepDTO object that gets added to database
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
        return matchingUsers.stream()
                .map(user -> createBulkStepDateDtoForUser(user, stringConverter.convert(startDate), stringConverter.convert(endDate)))
                .collect(Collectors.toList());
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
     * Updates the provided {@link Step} object in the database,
     * and creates new {@link WeekStep} and {@link MonthStep} objects if necessary based on whether a new week/month has started.
     *
     * @param step the {@link Step} object to be updated
     * @param stepData the {@link StepDTO} object containing the new step data
     * @return the updated {@link Step} object
     */
    private Step updateAndSaveStep(Step step, StepDTO stepData) {
        // Updates all relevant fields of the Step object in the database
        stepRepository.incrementStepCountAndUpdateTimes(step, stepData.getStepCount(), stepData.getEndTime(), stepData.getUploadTime());
        updateOrSaveNewWeekStep(stepData);
        updateOrSaveNewMonthStep(stepData);
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
     * Saves a {@link StepDTO} object to all relevant tables in the database.
     *
     * @param stepDTO the {@link StepDTO} object to save
     * @return the saved {@link StepDTO} object
     *
     * @see StepMapper#stepDtoToStep(StepDTO)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     */
    private Step saveToAllTables(StepDTO stepDTO) {
        var newStep = StepMapper.mapper.stepDtoToStep(stepDTO);
        stepRepository.save(newStep);
        weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDTO));
        monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDTO));
        return newStep;
    }
}
