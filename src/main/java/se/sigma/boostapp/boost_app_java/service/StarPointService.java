package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.BulkUserStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.StarPointDateDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StarPointService {

    /**
     * Temporary star point factor used during development
     */
    private static final double starPointFactor = 1;

    private final StepRepository stepRepository;

    public StarPointService(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }

    /**
     * Translate steps to star points for a list of users
     *
     * @param requestStarPointsDTO Data for star points for multiple users with start time and end time
     */
    public List<BulkUserStarPointsDTO> getStarPointsByMultipleUsers(RequestStarPointsDTO requestStarPointsDTO) {
        List<String> users = requestStarPointsDTO.getUsers();
        if(users == null || users.isEmpty()) {
            users = stepRepository.getAllUsers();
        }
        List<BulkUserStarPointsDTO> starPointsDTO = new ArrayList<>();
        for(String user: users) {
            var startTime = requestStarPointsDTO.getStartTime();
            var endTime = requestStarPointsDTO.getEndTime();
            var optionalSum = stepRepository.getStepCountSum(user, startTime, endTime);
            if(optionalSum.isPresent()) {
                starPointsDTO.add(new BulkUserStarPointsDTO(user, new StarPointDateDTO("Steps","Walking", startTime.toString(), endTime.toString(),
                        (int) Math.ceil(optionalSum.get() * starPointFactor))));
            }
        }
        return starPointsDTO;
    }
}
