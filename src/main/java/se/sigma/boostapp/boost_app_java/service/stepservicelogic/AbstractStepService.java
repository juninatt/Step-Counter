package se.sigma.boostapp.boost_app_java.service.stepservicelogic;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.UserStepListDTO;
import se.sigma.boostapp.boost_app_java.mapper.DateHelper;
import se.sigma.boostapp.boost_app_java.mapper.StepMapper;
import se.sigma.boostapp.boost_app_java.model.MonthStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import se.sigma.boostapp.boost_app_java.util.ObjectUpdater;
import se.sigma.boostapp.boost_app_java.util.Sorter;
import se.sigma.boostapp.boost_app_java.util.StringComparator;
import se.sigma.boostapp.boost_app_java.util.parser.StringToTimeStampParser;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public abstract class AbstractStepService {

    private final StepRepository stepRepository;
    private final MonthStepRepository monthStepRepository;
    private final WeekStepRepository weekStepRepository;
    private final Sorter sorter = new Sorter();


    public AbstractStepService(final StepRepository stepRepository,
                       final MonthStepRepository monthStepRepository,
                       final WeekStepRepository weekStepRepository) {
        this.stepRepository = stepRepository;
        this.monthStepRepository = monthStepRepository;
        this.weekStepRepository = weekStepRepository;
    }

    public Optional<Step> createOrUpdateStepForUser(String userId, StepDTO stepData) {
        try {
            Step latestStepForUser = getLatestStepFromUser(userId).orElse(null);
            assert latestStepForUser != null;
            return shouldCreateNewStep(stepData, latestStepForUser) ?
                    createAndSaveNewStepForUser(userId, stepData) :
                    updateAndSaveExistingStep(latestStepForUser, stepData);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Step> createAndSaveNewStepForUser(String userId, StepDTO stepDto) {
        if (addStepToWeekAndMonthTables(userId, stepDto)) {
            var step = StepMapper.mapper.stepDtoToStep(stepDto);
            step.setUserId(userId);
            return Optional.of(stepRepository.save(step));
        } else {
            return Optional.empty();
        }
    }

    private boolean addStepToWeekAndMonthTables(String userID, StepDTO stepDTO) {
        return addStepsToWeekTable(userID, stepDTO) &&
                addStepsToMonthTable(userID, stepDTO);
    }

    public boolean addStepsToWeekTable(String userId, StepDTO stepDTO) {
        var updater = ObjectUpdater.getInstance();
        boolean successfullyAdded = false;
        try {
            var week = DateHelper.getWeek(stepDTO.getEndTime());
            weekStepRepository.findByUserIdAndYearAndWeek(userId, stepDTO.getEndTime().getYear(), week)
                    .ifPresentOrElse(
                            weekStep -> weekStepRepository.save(updater.updateWeekStep(weekStep, stepDTO)),
                            () -> {
                                var weekStep = StepMapper.mapper.stepDtoToWeekStep(stepDTO);
                                weekStep.setUserId(userId);
                                weekStepRepository.save(weekStep);
                            }
                    );
            successfullyAdded = true;
        } catch (NullPointerException | DataAccessException exception) {
            exception.printStackTrace();
        } catch (DateTimeException exception) {
            exception.printStackTrace();
            weekStepRepository.save(new WeekStep(userId, 0, 0, stepDTO.getStepCount()));
        }
        return successfullyAdded;
    }

    private boolean addStepsToMonthTable(String userId, StepDTO stepDTO) {
        var updater = ObjectUpdater.getInstance();
        boolean successfullyAdded = false;
        try {
            monthStepRepository.findByUserIdAndYearAndMonth(userId, stepDTO.getYear(), stepDTO.getMonth())
                    .ifPresentOrElse(monthStep -> monthStepRepository.save(updater.updateMonthStep(monthStep, stepDTO)),
                            () -> {
                        var weekStep = StepMapper.mapper.stepDtoToMonthStep(stepDTO);
                        weekStep.setUserId(userId);
                        monthStepRepository.save(weekStep);
                            });
            successfullyAdded = true;
        } catch (NullPointerException | DataAccessException | ConversionFailedException exception) {
            exception.printStackTrace();
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            monthStepRepository.save(new MonthStep(userId, 0, 0, stepDTO.getStepCount()));
        }
        return successfullyAdded;
    }

    private Optional<Step> updateAndSaveExistingStep(Step currentStep, StepDTO stepDTO) {
        return updateExistingStepAndAddToTables(currentStep.getUserId(), stepDTO, currentStep) ?
                Optional.of(stepRepository.save(currentStep)) : Optional.empty();
    }
    private boolean updateExistingStepAndAddToTables(String userId, StepDTO stepDTO, Step existingStep) {
        var updater = ObjectUpdater.getInstance();
        return updater.updateExistingStep(existingStep, stepDTO) != null && addStepToWeekAndMonthTables(userId, stepDTO);
    }

    public List<StepDTO> registerMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        var existingStepOpt = stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
        return existingStepOpt.map(step -> registerAndSaveToExistingStepForUser(userId, stepDtoList, step))
                .orElseGet(() -> registerAndSaveFirstStepForUser(userId, stepDtoList));
    }

    private List<StepDTO> registerAndSaveToExistingStepForUser(String userId, List<StepDTO> stepDtoList, Step existingStep) {
        stepDtoList = sorter.collectStepDTOsWhereEndTimeIsAfter(stepDtoList, existingStep.getEndTime());
        stepDtoList.forEach(stepDTO -> updateExistingStepAndAddToTables(userId, stepDTO, existingStep));
        return stepDtoList;
    }

    private List<StepDTO> registerAndSaveFirstStepForUser(String  userId, List<StepDTO> stepDtoList) {
        var stepDTO = sorter.getOldestDTOFromList(stepDtoList);
        saveFirstStepForUser(userId, stepDTO);
        return stepDtoList;
    }

    private void saveFirstStepForUser(String userId, StepDTO stepDTO) {
        var step =StepMapper.mapper.stepDtoToStep(stepDTO);
        step.setUserId(userId);
        stepRepository.save(step);
        var weekStep =StepMapper.mapper.stepDtoToWeekStep(stepDTO);
        weekStep.setUserId(userId);
        weekStepRepository.save(weekStep);
        var monthStep =StepMapper.mapper.stepDtoToMonthStep(stepDTO);
        monthStep.setUserId(userId);
        monthStepRepository.save(monthStep);
    }

    public Optional<List<UserStepListDTO>> getMultipleUserStepListDTOs(List<String> users, String startDate, String endDate) {
        var parser = new StringToTimeStampParser();
        var matchingUsers = StringComparator.getMatching(users, stepRepository.getListOfAllDistinctUserId());
        var usersStepDTOs = createMultipleUserStepListDTOs(new ArrayList<>(matchingUsers), parser.convert(startDate), parser.convert(endDate));
        return matchingUsers.isEmpty() ?
                Optional.empty() :
                Optional.of(usersStepDTOs);
    }

    public Optional<Integer> getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return Optional.of(monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month)
                .orElse(0));
    }

    public Optional<Integer> getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return Optional.of(weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week)
                .orElse(0));
    }

    public Optional<Step> getLatestStepFromUser(String userId) {
        return Optional.of(stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId)
                .orElse(new Step(userId, 0, LocalDateTime.now())));
    }

    public Optional<List<StepDateDTO>> getListOfStepDataForCurrentWeekFromUser(String userId) {
        LocalDateTime now = LocalDateTime.now();
        var stepList = stepRepository.getStepsByUserIdAndEndTimeBetween(userId, now.with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()), now.with(DayOfWeek.SUNDAY).atZone(ZoneId.systemDefault()))
                .orElse(List.of(new Step(userId, 0, now)));
        return Optional.of(createStepDateDTOsForUser(stepList));
    }

    private List<UserStepListDTO> createMultipleUserStepListDTOs(List<String> users, Timestamp firstDate, Timestamp lastDate) {
        List<UserStepListDTO> userStepListDTOList = new ArrayList<>();
        users.forEach(user -> userStepListDTOList.add(new UserStepListDTO(user, stepRepository.getStepDataByUserIdAndDateRange(user, firstDate, lastDate))));
        return userStepListDTOList;
    }

    private List<StepDateDTO> createStepDateDTOsForUser(List<Step> steps) {
        return steps.stream()
                .map(step -> {
                    var dto = StepMapper.mapper.stepToStepDateDto(step);
                    dto.setDayOfWeek(step.getEndTime().getDayOfWeek().getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    private boolean shouldCreateNewStep(@NotNull StepDTO stepDto, @NotNull Step existingStep) {
        return existingStep.getStepCount() == 0 || !existingStep.getEndTime().isBefore(stepDto.getEndTime());
    }

    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }
}
