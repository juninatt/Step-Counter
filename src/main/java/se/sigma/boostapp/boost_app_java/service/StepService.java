package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.BulkStepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.MonthStepRepository;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;
import se.sigma.boostapp.boost_app_java.repository.WeekStepRepository;
import se.sigma.boostapp.boost_app_java.service.stepservicelogic.AbstractStepService;

import java.util.List;
import java.util.Optional;


@Service
public class StepService extends AbstractStepService {

    /**
     * Constructor
     *
     * @param stepRepository      {@link StepRepository}
     * @param monthStepRepository {@link MonthStepRepository}
     * @param weekStepRepository  {@link WeekStepRepository}
     */
    public StepService(StepRepository stepRepository, MonthStepRepository monthStepRepository, WeekStepRepository weekStepRepository) {
        super(stepRepository, monthStepRepository, weekStepRepository);
    }

    @Override
    public Optional<Step> addSingleStepForUser(String userId, StepDTO stepData) {
        return super.addSingleStepForUser(userId, stepData);
    }

    @Override
    public Optional<Step> getLatestStepFromUser(String userId) {
        return super.getLatestStepFromUser(userId);
    }

    @Override
    public List<StepDTO> addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        return super.addMultipleStepsForUser(userId, stepDtoList);
    }

    @Override
    public Optional<List<BulkStepDateDTO>> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        return super.filterUsersAndCreateListOfBulkStepDateDtoWithRange(users, startDate, endDate);
    }

    @Override
    public void addStepsToWeekTable(StepDTO stepDto) {
        super.addStepsToWeekTable(stepDto);
    }

    @Override
    public Optional<Integer> getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return super.getStepCountForUserYearAndMonth(userId, year, month);
    }

    @Override
    public Optional<Integer> getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return super.getStepCountForUserYearAndWeek(userId, year, week);
    }

    public Optional<BulkStepDateDTO> createBulkStepDateDtoForUserForCurrentWeek(String userId) {
        return super.createBulkStepDateDtoForUserForCurrentWeek(userId);
    }

    @Override
    public void deleteStepTable() {
        super.deleteStepTable();
    }
}
