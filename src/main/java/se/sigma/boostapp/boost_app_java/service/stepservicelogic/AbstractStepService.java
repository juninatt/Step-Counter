package se.sigma.boostapp.boost_app_java.service.stepservicelogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.BulkStepDateDTO;
import se.sigma.boostapp.boost_app_java.mapper.DateHelper;
import se.sigma.boostapp.boost_app_java.mapper.StepMapper;
import se.sigma.boostapp.boost_app_java.model.BoostAppStep;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import se.sigma.boostapp.boost_app_java.util.StepDtoSorter;
import se.sigma.boostapp.boost_app_java.util.StepUpdater;
import se.sigma.boostapp.boost_app_java.util.StringComparator;
import se.sigma.boostapp.boost_app_java.util.parser.StringToTimeStampParser;

import java.sql.Timestamp;
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
    private final StepDtoSorter sorter = new StepDtoSorter();


    public AbstractStepService(final StepRepository stepRepository,
                       final MonthStepRepository monthStepRepository,
                       final WeekStepRepository weekStepRepository) {
        this.stepRepository = stepRepository;
        this.monthStepRepository = monthStepRepository;
        this.weekStepRepository = weekStepRepository;
    }

    public Optional<Step> addSingleStepForUser(String userId, StepDTO stepData) {
        if (userId == null || stepData == null)
            return Optional.empty();
        else {
            stepData.setUserId(userId);
            return getLatestStepFromUser(userId).map(step -> updateAndSaveStepForUser(step, stepData))
                    .orElseGet(() -> createAndSaveStepForUser(stepData));
        }
    }

    public List<StepDTO> addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        if (userId == null || stepDtoList == null) {
            return List.of(new StepDTO("Incorrect data. Try again", LocalDateTime.now()));
        }
        else {
            stepDtoList.forEach(stepDto -> stepDto.setUserId(userId));
            return getLatestStepFromUser(userId).map(step -> filterStepDtoListAndUpdateStep(stepDtoList, step))
                    .orElseGet(() -> List.of(saveToAllTables(sorter.getOldest(stepDtoList))));
        }
    }

    private Optional<Step> createAndSaveStepForUser(StepDTO stepDto) {
        addStepToWeekAndMonthTables(stepDto);
        return Optional.of(stepRepository.save(StepMapper.mapper.stepDtoToStep(stepDto)));
    }

    private Optional<Step> updateAndSaveStepForUser(Step currentStep, StepDTO stepDto) {
        updateAndSaveStep(currentStep, stepDto, stepRepository);
        addStepToWeekAndMonthTables(stepDto);
        return Optional.of(stepRepository.save(currentStep));
    }

    private <T extends BoostAppStep> BoostAppStep updateAndSaveStep(T step, StepDTO stepDto, JpaRepository<T, Long> repository) {
        var updatedStep = StepUpdater.getInstance().update(step, stepDto);
        return saveStep(updatedStep, repository);
    }

    private <T extends BoostAppStep> BoostAppStep saveStep(T step, JpaRepository<T, Long> repository) {
        return repository.save(step);
    }


    private void addStepToWeekAndMonthTables(StepDTO stepDto) {
         addStepsToWeekTable(stepDto);
         addStepsToMonthTable(stepDto);
    }

    public void addStepsToWeekTable(StepDTO stepDto) {
        var week = DateHelper.getWeek(stepDto.getEndTime());
            weekStepRepository.findByUserIdAndYearAndWeek(stepDto.getUserId(), stepDto.getEndTime().getYear(), week)
                    .ifPresentOrElse(weekStep -> updateAndSaveStep(weekStep, stepDto, weekStepRepository),
                            () -> weekStepRepository.save(StepMapper.mapper.stepDtoToWeekStep(stepDto)));
    }

    private void addStepsToMonthTable(StepDTO stepDto) {
        monthStepRepository.findByUserIdAndYearAndMonth(stepDto.getUserId(), stepDto.getYear(), stepDto.getMonth())
                    .ifPresentOrElse(monthStep -> updateAndSaveStep(monthStep, stepDto, monthStepRepository),
                            () -> monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto)));
    }


    private List<StepDTO> filterStepDtoListAndUpdateStep(List<StepDTO> stepDtoList, Step currentStep) {
        stepDtoList = sorter.collectEndTimeIsAfter(stepDtoList, currentStep.getEndTime());
        stepDtoList.forEach(stepDto -> updateAndSaveStepForUser(currentStep, stepDto));
        return stepDtoList;
    }


    private StepDTO saveToAllTables(StepDTO stepDto) {
        saveStep(StepMapper.mapper.stepDtoToStep(stepDto), stepRepository);
        saveStep(StepMapper.mapper.stepDtoToWeekStep(stepDto), weekStepRepository);
        saveStep(StepMapper.mapper.stepDtoToMonthStep(stepDto), monthStepRepository);
        return stepDto;
    }

    public Optional<List<BulkStepDateDTO>> getMultipleUserStepListDTOs(List<String> users, String startDate, String endDate) {
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
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
    }

    public Optional<List<StepDateDTO>> getListOfStepDataForCurrentWeekFromUser(String userId) {
        LocalDateTime now = LocalDateTime.now();
        var stepList = stepRepository.getStepsByUserIdAndEndTimeBetween(userId, now.with(DayOfWeek.MONDAY).atZone(ZoneId.systemDefault()), now.with(DayOfWeek.SUNDAY).atZone(ZoneId.systemDefault()))
                .orElse(List.of(new Step(userId, 0, now)));
        return Optional.of(createStepDateDTOsForUser(stepList));
    }

    private List<BulkStepDateDTO> createMultipleUserStepListDTOs(List<String> users, Timestamp firstDate, Timestamp lastDate) {
        List<BulkStepDateDTO> bulkStepListDTODate = new ArrayList<>();
        users.forEach(user -> bulkStepListDTODate.add(new BulkStepDateDTO(user, stepRepository.getStepDataByUserIdAndDateRange(user, firstDate, lastDate))));
        return bulkStepListDTODate;
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

    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }
}
