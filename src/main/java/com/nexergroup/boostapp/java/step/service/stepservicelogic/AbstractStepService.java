package com.nexergroup.boostapp.java.step.service.stepservicelogic;

import com.nexergroup.boostapp.java.step.mapper.DateHelper;
import com.nexergroup.boostapp.java.step.mapper.StepMapper;
import com.nexergroup.boostapp.java.step.model.BoostAppStep;
import com.nexergroup.boostapp.java.step.model.MonthStep;
import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.model.WeekStep;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;
import com.nexergroup.boostapp.java.step.util.StepDtoSorter;
import com.nexergroup.boostapp.java.step.util.StepUpdater;
import com.nexergroup.boostapp.java.step.util.StringComparator;
import com.nexergroup.boostapp.java.step.util.parser.StringToTimeStampConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
 * @see StepDtoSorter
 */
@Component
public abstract class AbstractStepService {

    private final StepRepository stepRepository;
    private final WeekStepRepository weekStepRepository;
    private final MonthStepRepository monthStepRepository;
    private final StepDtoSorter sorter = new StepDtoSorter();


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
     * Adds a step data to a specified user in form of a {@link StepDTO} object.
     *
     * @param userId the ID of the user
     * @param stepData a {@link StepDTO} object holding the data to be added
     * @return an Optional containing the added {@link Step} object or an empty Optional if the provided data is invalid
     *
     * @see StepRepository
     * @see StepMapper#stepDtoToStep(StepDTO)
     */
    public Optional<Step> addSingleStepForUser(String userId, StepDTO stepData) {
        if (userId == null || !isValidDto(stepData))
            return Optional.of(new Step("Invalid Data",0,  LocalDateTime.now()));
        else {
            stepData.setUserId(userId);
            return getLatestStepFromUser(userId).map(step -> updateAndSaveStepForUser(step, stepData, stepRepository))
                    .orElseGet(() -> Optional.of(StepMapper.mapper.stepDtoToStep(saveToAllTables(stepData))));
        }
    }


    /**
     * This method adds data from multiple {@link StepDTO} objects for a specified user.
     * If input is invalid, returns a list with single step data object with message "Invalid Data".
     * Otherwise, adds the userId to each {@link StepDTO} object,
     * filters the list of objects based on latest {@link Step} object of the user
     * and updates the step data in the database and returns the filtered step data list.
     *
     * @param userId the ID of the user
     * @param stepDtoList the list of {@link StepDTO} objects
     * @return the filtered list of {@link StepDTO} objects
     *
     * @see StepDtoSorter#getOldest(List)
     */
    public List<StepDTO> addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        if (userId == null || isValidDtoList(stepDtoList)) {
            return List.of(new StepDTO("Invalid Data", LocalDateTime.now()));
        }
        else {
            stepDtoList.forEach(stepDto -> stepDto.setUserId(userId));
            return getLatestStepFromUser(userId).map(step -> filterStepDtoListAndUpdateStep(stepDtoList, step))
                    .orElseGet(() -> List.of(saveToAllTables(sorter.getOldest(stepDtoList))));
        }
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
     * Updates the given {@link BoostAppStep} and saves the updated step to the specified repository.
     *
     * @param currentStep the current step object to be updated
     * @param stepDto the {@link StepDTO} object containing the data to update the current step
     * @param repository the repository where the updated step will be saved
     * @return an Optional {@link BoostAppStep} object containing the updated step
     *
     * @see StepUpdater#update(BoostAppStep, StepDTO)
     */
    private <T extends BoostAppStep> Optional<T> updateAndSaveStepForUser(T currentStep, StepDTO stepDto, JpaRepository<T, Long> repository) {
        var updatedStep = StepUpdater.getInstance().update(currentStep, stepDto);
        updateOrSaveNewWeekStep(stepDto);
        updateOrSaveNewMonthStep(stepDto);
        return Optional.of(repository.save(updatedStep));
    }

    /**
     * Saves the given {@link BoostAppStep} to the specified repository.
     *
     * @param step the step object to be saved
     * @param repository the repository where the step will be saved
     * @return the saved {@link BoostAppStep} object
     *
     */
    private <T extends BoostAppStep> BoostAppStep saveBoostAppStepStep(T step, JpaRepository<T, Long> repository) {
        return repository.save(step);
    }

    /**
     * Updates or saves a new {@link WeekStep} object in the database.
     * If a week step with the same user ID, year, and week exists in the database,
     * the step is updated using the {@link StepUpdater}.
     * If it does not exist, a new week step is saved.
     *
     * @param stepDto the {@link StepDTO} object containing the step data
     *
     * @see DateHelper#getWeek(LocalDateTime)
     * @see WeekStepRepository#findByUserIdAndYearAndWeek(String, int, int)
     * @see StepUpdater#update(BoostAppStep, StepDTO)
     * @see StepMapper#stepDtoToWeekStep(StepDTO)
     */
    private void updateOrSaveNewWeekStep(StepDTO stepDto) {
        var week = DateHelper.getWeek(stepDto.getEndTime());
            weekStepRepository.findByUserIdAndYearAndWeek(stepDto.getUserId(), stepDto.getEndTime().getYear(), week)
                    .ifPresentOrElse(weekStep -> saveBoostAppStepStep(StepUpdater.getInstance().update(weekStep, stepDto), weekStepRepository),
                            () -> saveBoostAppStepStep(StepMapper.mapper.stepDtoToWeekStep(stepDto), weekStepRepository));
    }

    /**
     * Updates or saves a new {@link MonthStep} step in the database.
     * If a month step with the same user ID, year, and month exists in the database,
     * the step is updated using the {@link StepUpdater}.
     * If it does not exist, a new month step is saved.
     *
     * @param stepDto the {@link StepDTO} object containing the step data
     *
     * @see MonthStepRepository#findByUserIdAndYearAndMonth(String, int, int)
     * @see StepUpdater#update(BoostAppStep, StepDTO)
     * @see StepMapper#stepDtoToMonthStep(StepDTO)
     */
    private void updateOrSaveNewMonthStep(StepDTO stepDto) {
        monthStepRepository.findByUserIdAndYearAndMonth(stepDto.getUserId(), stepDto.getYear(), stepDto.getMonth())
                    .ifPresentOrElse(monthStep -> saveBoostAppStepStep(StepUpdater.getInstance().update(monthStep, stepDto), monthStepRepository),
                            () -> monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto)));
    }

    /**
     * Filters a list of {@link StepDTO} objects and updates each step in the database.
     * The {@link StepDTO} objects are sorted so that only the ones with an endTime after
     * the current step's end time are included in the filtered list.
     *
     * @param stepDtoList the list of {@link StepDTO} objects
     * @param currentStep the current {@link Step} object to use as a reference for filtering and updating
     * @return the filtered list of {@link StepDTO} objects
     *
     * @see StepDtoSorter#collectEndTimeIsAfter(List, LocalDateTime)
     */
    private List<StepDTO> filterStepDtoListAndUpdateStep(List<StepDTO> stepDtoList, Step currentStep) {
        stepDtoList = sorter.collectEndTimeIsAfter(stepDtoList, currentStep.getEndTime());
        stepDtoList.forEach(stepDto -> updateAndSaveStepForUser(currentStep, stepDto, stepRepository));
        return stepDtoList;
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
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToStep(stepDto), stepRepository);
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToWeekStep(stepDto), weekStepRepository);
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToMonthStep(stepDto), monthStepRepository);
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
        if (stepDtoList != null) {
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
