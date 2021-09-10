package se.sigma.boostapp.boost_app_java.service;

import org.springframework.stereotype.Service;
import se.sigma.boostapp.boost_app_java.dto.BulkUserStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.StarPointDateDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

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

        if (requestStarPointsDTO.getUsers() == null) {
            requestStarPointsDTO.setUsers(stepRepository.getAllUsers());
        }
        return requestStarPointsDTO
                .getUsers()
                .stream()
                .filter(user -> (stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).isPresent())
                .map(user -> new BulkUserStarPointsDTO(user, new StarPointDateDTO(
                        "Steps",
                        "Walking",
                        requestStarPointsDTO.getStartTime().toString(),
                        requestStarPointsDTO.getEndTime().toString(),
                        (int) Math.ceil(
                                (stepRepository.getStepCountSum(user, requestStarPointsDTO.getStartTime(), requestStarPointsDTO.getEndTime())).get()
                                        * starPointFactor))))
                .collect(Collectors.toList());
    }

}
