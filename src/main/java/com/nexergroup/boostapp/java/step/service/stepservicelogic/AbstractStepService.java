package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.builder.StepDTOBuilder;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
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
     * Adds step data for a specified user in the form of a {@link StepDTO} object.
     *
     * @param userId the ID of the user
     * @param stepData the {@link StepDTO} object holding the step data to be added
     * @return an Optional containing the added {@link Step} object or a default 'Invalid Step'-object if the provided data is invalid
     *
     * @see StepRepository
     * @see StepMapper#stepDtoToStep(StepDTO)
     */
    public Optional<Step> addSingleStepForUser(String userId, StepDTO stepData) {
        if (userId == null || !isValidDto(stepData))
            return Optional.of(new Step("Invalid Data",0,  LocalDateTime.now()));
        else {
            stepData.setUserId(userId);
            return getLatestStepFromUser(userId).map(step -> updateAndSaveStep(step, stepData))
                    .or(() -> Optional.of(StepMapper.mapper.stepDtoToStep(saveToAllTables(stepData))));
        }
    }

    /**
     * This method adds data from multiple {@link StepDTO} objects for a specified user.
     * If input is invalid, returns a list with a default 'Invalid StepDTO' object,
     * otherwise it gathers the information from the {@link StepDTO} objects and creates a new single
     * {@link StepDTO} object containing all the data, then converts the DTO to a {@link Step} and saves it to the database.
     *
     * @param userId the ID of the user
     * @param stepDtoList the list of {@link StepDTO} objects
     * @return a {@link StepDTO} object holding all the data from the objects in the stepDtoList
     *
     */
    public StepDTO addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        if (userId == null || !isValidDtoList(stepDtoList)) {
            return new StepDTO("Invalid Data", LocalDateTime.now());
        }
        else {
            var gatheredData = gatherStepDataForUser(stepDtoList, userId);
            return StepMapper.mapper.stepToStepDTO(addSingleStepForUser(userId, gatheredData).get());
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
    public Optional<Step> getLatestStepFromUser(String userId) {
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
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
    public Optional<Integer> getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return Optional.of(monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month)
                .orElse(0));
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
    public Optional<Integer> getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return Optional.of(weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week)
                .orElse(0));
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
    public Optional<List<BulkStepDateDTO>> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        var stringConverter = new StringToTimeStampConverter();
        var matchingUsers = StringComparator.getMatching(users, stepRepository.getListOfAllDistinctUserId());
        var listOfBulkStepDateDto = matchingUsers.stream()
                .map(user -> createBulkStepDateDtoForUser(user, stringConverter.convert(startDate), stringConverter.convert(endDate))
                        .orElse(new BulkStepDateDTO("Invalid Data", List.of())))
                .collect(Collectors.toList());
        return Optional.of(listOfBulkStepDateDto);
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
        var listOfUserStepDateDto = stepRepository.getStepDataByUserIdAndDateRange(userId, Timestamp.valueOf(weekStart.toLocalDateTime()), Timestamp.valueOf(weekEnd.toLocalDateTime()));
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
    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUser(String userId, Timestamp startTime, Timestamp endTime) {
        var listOfStepsDateDtoForUser = stepRepository.getStepDataByUserIdAndDateRange(userId, startTime, endTime);
        return Optional.of(new BulkStepDateDTO(userId, listOfStepsDateDtoForUser));
    }

    /**
     * Updates the given {@link Step} object with the stepCount, endTime and uploadTime from the {@link StepDTO} object.
     * Saves the updated {@link Step} object and creates new {@link WeekStep} and {@link MonthStep} objects if necessary.
     * Returns the latest {@link Step} object for the user.
     *
     * @param step the {@link Step} object to be updated
     * @param stepData the {@link StepDTO} object containing the data to update the {@link Step} object with
     * @return the latest {@link Step} object for the user
     */
    private Step updateAndSaveStep(Step step, StepDTO stepData) {
        stepRepository.incrementStepCountAndUpdateTimes(step, stepData.getStepCount(), stepData.getEndTime(), stepData.getUploadTime());
        updateOrSaveNewWeekStep(stepData);
        updateOrSaveNewMonthStep(stepData);
        return getLatestStepFromUser(step.getUserId()).get();
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
    private StepDTO saveToAllTables(StepDTO stepDto) {
        stepRepository.save(StepMapper.mapper.stepDtoToStep(stepDto));
        weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDto));
        monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto));
        return stepDto;
    }

    /**
     * Verifies that the given list of {@link StepDTO} objects are valid.
     * A list is considered valid if the list containing the objects is not null
     * and if the {@link StepDTO} objects in the list passes the requirements
     * of the {@link AbstractStepService#isValidDto(StepDTO)} method
     *
     * @param stepDtoList the list of {@link StepDTO} objects to be verified.
     * @return true if the list is valid, otherwise false.
     */
    private boolean isValidDtoList(List<StepDTO> stepDtoList) {
        if (stepDtoList == null)
            return false;
        else {
            for (StepDTO dto : stepDtoList) {
                if (!isValidDto(dto))
                    return false;
            }
        }
        return true;
    }

    /**
     * Verifies if the given {@link StepDTO} object is valid.
     * A {@link StepDTO} is considered valid if it is not null and all its properties
     * 'startTime', 'endTime' and 'uploadTime' are not null.
     * Additionally, the 'endTime' must be after 'startTime' and
     * the 'uploadTime' must be after 'endTime'.
     *
     * @param stepDto the {@link StepDTO} object to be verified.
     * @return true if the object is valid, otherwise false.
     */
    private boolean isValidDto(StepDTO stepDto) {
        return stepDto != null
                && stepDto.getStartTime() != null
                && stepDto.getEndTime() != null
                && stepDto.getUploadTime() != null
                && stepDto.getEndTime().isAfter(stepDto.getStartTime())
                && stepDto.getUploadTime().isAfter(stepDto.getEndTime());
    }
}
