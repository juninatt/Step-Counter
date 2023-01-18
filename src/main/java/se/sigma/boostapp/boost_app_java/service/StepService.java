package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.dto.stepdto.UserStepListDTO;
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
    public Optional<Step> createOrUpdateStepForUser(String userId, StepDTO stepData) {
        return super.createOrUpdateStepForUser(userId, stepData);
    }

    @Override
    public Optional<Step> getLatestStepFromUser(String userId) {
        return super.getLatestStepFromUser(userId);
    }

    @Override
    public List<StepDTO> registerMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        return super.registerMultipleStepsForUser(userId, stepDtoList);
    }

    @Override
    public Optional<List<UserStepListDTO>> getMultipleUserStepListDTOs(List<String> users, String startDate, String endDate) {
        return super.getMultipleUserStepListDTOs(users, startDate, endDate);
    }

    @Override
    public boolean addStepsToWeekTable(String userId, StepDTO stepDTO) {
        return super.addStepsToWeekTable(userId, stepDTO);
    }

    @Override
    public Optional<Integer> getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return super.getStepCountForUserYearAndMonth(userId, year, month);
    }

    @Override
    public Optional<Integer> getStepCountForUserYearAndWeek(String userId, int year, int week) {
        return super.getStepCountForUserYearAndWeek(userId, year, week);
    }

    @Override
    public Optional<List<StepDateDTO>> getListOfStepDataForCurrentWeekFromUser(String userId) {
        return super.getListOfStepDataForCurrentWeekFromUser(userId);
    }

    @Override
    public void deleteStepTable() {
        super.deleteStepTable();
    }
}
