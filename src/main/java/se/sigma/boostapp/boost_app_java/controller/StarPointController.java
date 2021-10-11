package se.sigma.boostapp.boost_app_java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sigma.boostapp.boost_app_java.dto.BulkUserStarPointsDTO;
import se.sigma.boostapp.boost_app_java.dto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.service.StarPointService;

import java.util.List;

@RestController
@RequestMapping("/starpoints")
public class StarPointController {

    private final StarPointService starPointService;


    public StarPointController(StarPointService starPointService) {
        this.starPointService= starPointService;
    }
/**
 * Post request <br>
 * Get sum of star points by a start date and end date for a list of users. Return sum of all users if empty or absent list.
 * @param requestStarPointsDTO Data for star points for multiple users with start time and end time
 * @return A list of BulkUserStarPointsDTO:s
 */
    @Operation(summary = "Get sum of star points by a start date and end date for a list of users.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully post request"),
            @ApiResponse(responseCode = "401", description = "Request is not authorized"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found") })
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BulkUserStarPointsDTO> getStarPointsByUsers(final @RequestBody RequestStarPointsDTO requestStarPointsDTO) {
        return starPointService.getStarPointsByMultipleUsers(requestStarPointsDTO);
    }
}
