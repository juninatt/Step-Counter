package com.nexergroup.boostapp.java.step.service;

import com.nexergroup.boostapp.java.step.model.Step;
import com.nexergroup.boostapp.java.step.repository.MonthStepRepository;
import com.nexergroup.boostapp.java.step.repository.StepRepository;
import com.nexergroup.boostapp.java.step.repository.WeekStepRepository;
import com.nexergroup.boostapp.java.step.service.stepservicelogic.AbstractStepService;
import org.springframework.stereotype.Service;
import com.nexergroup.boostapp.java.step.dto.stepdto.BulkStepDateDTO;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;

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
    public Step addSingleStepForUser(String userId, StepDTO stepData) {
        return super.addSingleStepForUser(userId, stepData);
    }

    @Override
    public Step getLatestStepFromUser(String userId) {
        return super.getLatestStepFromUser(userId);
    }

    @Override
    public Step addMultipleStepsForUser(String userId, List<StepDTO> stepDtoList) {
        return super.addMultipleStepsForUser(userId, stepDtoList);
    }

    @Override
    public List<BulkStepDateDTO> filterUsersAndCreateListOfBulkStepDateDtoWithRange(List<String> users, String startDate, String endDate) {
        return super.filterUsersAndCreateListOfBulkStepDateDtoWithRange(users, startDate, endDate);
    }

    @Override
    public Integer getStepCountForUserYearAndMonth(String userId, int year, int month) {
        return super.getStepCountForUserYearAndMonth(userId, year, month);
    }

    @Override
    public Integer getStepCountForUserYearAndWeek(String userId, int year, int week) {
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
