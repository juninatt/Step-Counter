package se.sigma.boostapp.boost_app_java.service.stepservicelogic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.dto.stepdto.BulkStepDateDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
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
import se.sigma.boostapp.boost_app_java.util.parser.StringToTimeStampConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void deleteStepTable() {
        stepRepository.deleteAllFromStep();
    }

    public Optional<Step> addSingleStepForUser(String userId, StepDTO stepData) {
        if (userId == null || !isValidDto(stepData))
            return Optional.of(new Step("Invalid Data",0,  LocalDateTime.now()));
        else {
            stepData.setUserId(userId);
            return getLatestStepFromUser(userId).map(step -> updateAndSaveStepForUser(step, stepData, stepRepository))
                    .orElseGet(() -> Optional.of(StepMapper.mapper.stepDtoToStep(saveToAllTables(stepData))));
        }
    }

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

    public Optional<Step> getLatestStepFromUser(String userId) {
        return stepRepository.findFirstByUserIdOrderByEndTimeDesc(userId);
    }


    public Optional<Integer> getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return Optional.of(monthStepRepository.getStepCountByUserIdYearAndMonth(userId, year, month)
                .orElse(0));
    }

    public Optional<Integer> getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return Optional.of(weekStepRepository.getStepCountByUserIdYearAndWeek(userId, year, week)
                .orElse(0));
    }

    public Optional<List<BulkStepDateDTO>> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        var converter = new StringToTimeStampConverter();
        var matchingUsers = StringComparator.getMatching(users, stepRepository.getListOfAllDistinctUserId());
        var listOfBulkStepDateDto = matchingUsers.stream()
                .map(user -> createBulkStepDateDtoForUser(user, converter.convert(startDate), converter.convert(endDate))
                        .orElse(new BulkStepDateDTO("Invalid Data", List.of())))
                .collect(Collectors.toList());
        return Optional.of(listOfBulkStepDateDto);
    }

    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUserForCurrentWeek(String userId) {
        var weekStart = DateHelper.getWeekStart(LocalDateTime.now(), ZoneId.systemDefault());
        var weekEnd = DateHelper.getWeekEnd(LocalDateTime.now(), ZoneId.systemDefault());
        var listOfUserStepDateDto = stepRepository.getStepDataByUserIdAndDateRange(userId, Timestamp.valueOf(weekStart.toLocalDateTime()), Timestamp.valueOf(weekEnd.toLocalDateTime()));
        return Optional.of(new BulkStepDateDTO(userId, listOfUserStepDateDto));
    }

    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUser(String userId, Timestamp startTime, Timestamp endTime) {
        var listOfStepsDateDtoForUser = stepRepository.getStepDataByUserIdAndDateRange(userId, startTime, endTime);
        return Optional.of(new BulkStepDateDTO(userId, listOfStepsDateDtoForUser));
    }

    private <T extends BoostAppStep> Optional<T> updateAndSaveStepForUser(T currentStep, StepDTO stepDto, JpaRepository<T, Long> repository) {
        var updatedStep = StepUpdater.getInstance().update(currentStep, stepDto);
        updateOrSaveNewWeekStep(stepDto);
        updateOrSaveNewMonthStep(stepDto);
        return Optional.of(repository.save(updatedStep));
    }

    private <T extends BoostAppStep> BoostAppStep saveBoostAppStepStep(T step, JpaRepository<T, Long> repository) {
        return repository.save(step);
    }

    private void updateOrSaveNewWeekStep(StepDTO stepDto) {
        var week = DateHelper.getWeek(stepDto.getEndTime());
            weekStepRepository.findByUserIdAndYearAndWeek(stepDto.getUserId(), stepDto.getEndTime().getYear(), week)
                    .ifPresentOrElse(weekStep -> saveBoostAppStepStep(StepUpdater.getInstance().update(weekStep, stepDto), weekStepRepository),
                            () -> saveBoostAppStepStep(StepMapper.mapper.stepDtoToWeekStep(stepDto), weekStepRepository));
    }

    private void updateOrSaveNewMonthStep(StepDTO stepDto) {
        monthStepRepository.findByUserIdAndYearAndMonth(stepDto.getUserId(), stepDto.getYear(), stepDto.getMonth())
                    .ifPresentOrElse(monthStep -> saveBoostAppStepStep(StepUpdater.getInstance().update(monthStep, stepDto), monthStepRepository),
                            () -> monthStepRepository.save(StepMapper.mapper.stepDtoToMonthStep(stepDto)));
    }


    private List<StepDTO> filterStepDtoListAndUpdateStep(List<StepDTO> stepDtoList, Step currentStep) {
        stepDtoList = sorter.collectEndTimeIsAfter(stepDtoList, currentStep.getEndTime());
        stepDtoList.forEach(stepDto -> updateAndSaveStepForUser(currentStep, stepDto, stepRepository));
        return stepDtoList;
    }

    private StepDTO saveToAllTables(StepDTO stepDto) {
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToStep(stepDto), stepRepository);
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToWeekStep(stepDto), weekStepRepository);
        saveBoostAppStepStep(StepMapper.mapper.stepDtoToMonthStep(stepDto), monthStepRepository);
        return stepDto;
    }
    private boolean isValidDtoList(List<StepDTO> stepDtoList) {
        if (stepDtoList != null) {
            for (StepDTO dto : stepDtoList) {
                if (!isValidDto(dto))
                    return false;
            }
        }
        return true;
    }
    private boolean isValidDto(StepDTO dto) {
        return dto != null
                && dto.getStartTime() != null
                && dto.getEndTime() != null
                && dto.getUploadTime() != null
                && dto.getEndTime().isAfter(dto.getStartTime())
                && dto.getUploadTime().isAfter(dto.getEndTime());
    }
}
