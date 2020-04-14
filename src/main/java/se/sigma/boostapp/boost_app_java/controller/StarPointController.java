package se.sigma.boostapp.boost_app_java.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.sigma.boostapp.boost_app_java.dto.BulkUserStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.service.StepService;

import java.util.List;

@RestController
@RequestMapping("/starpoints")
public class StarPointController {

    private final StepService stepService;

    public StarPointController(StepService stepService) {
        this.stepService = stepService;
    }

    @ApiOperation(value = "Get sum of star points by a start date and end date for a list of users.", response = List.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully post request"),
            @ApiResponse(code = 401, message = "Request is not authorized"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BulkUserStarPointsDTO> getStarPointsByUsers(final @RequestBody RequestStarPointsDTO requestStarPointsDTO) {
        return stepService.getStarPointsByMultipleUsers(requestStarPointsDTO);
    }

    @PutMapping("/hej")
    public String getHej(@RequestBody String hej) {
        return hej;
    }

}
