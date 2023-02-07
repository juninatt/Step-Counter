package com.nexergroup.boostapp.java.step.service;

import com.nexergroup.boostapp.java.step.repository.StepRepository;
import org.springframework.stereotype.Service;
import com.nexergroup.boostapp.java.step.dto.starpointdto.BulkUserStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.RequestStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.StarPointDateDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class that handles the calculation and retrieval of star points for users
 */
@Service
public class StarPointService {

    /**
     * Temporary star point factor used during development
     */
    private static final double starPointFactor = 1;

    private final StepRepository stepRepository;

    /**
     * Constructor for injecting the step repository
     *
     * @param stepRepository Repository for retrieving step count data
     */
    public StarPointService(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    /**
     * Translate steps to star points for a list of users
     *
     * @param requestStarPointsDTO Data for star points for multiple users with start time and end time
     * @return List of {@link BulkUserStarPointsDTO} objects containing star point data
     */
    public List<BulkUserStarPointsDTO> getStarPointsByMultipleUsers(RequestStarPointsDTO requestStarPointsDTO) {
        List<String> users = requestStarPointsDTO.getUsers();
        if(users == null || users.isEmpty()) {
            users = stepRepository.getListOfAllDistinctUserId();
        }
        List<BulkUserStarPointsDTO> starPointsDTO = new ArrayList<>();
        for(String user: users) {
            var startTime = requestStarPointsDTO.getStartTime();
            var endTime = requestStarPointsDTO.getEndTime();
            var optionalSum = stepRepository.getStepCountByUserIdAndDateRange(user, startTime, endTime);
            optionalSum.ifPresent(integer -> starPointsDTO.add(new BulkUserStarPointsDTO(user, new StarPointDateDTO("Steps", "Walking", startTime.toString(), endTime.toString(),
                    (int) Math.ceil(integer * starPointFactor)))));
        }
        return starPointsDTO;
    }
}
