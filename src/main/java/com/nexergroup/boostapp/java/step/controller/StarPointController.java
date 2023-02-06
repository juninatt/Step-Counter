package com.nexergroup.boostapp.java.step.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nexergroup.boostapp.java.step.dto.starpointdto.BulkUserStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.RequestStarPointsDTO;
import com.nexergroup.boostapp.java.step.service.StarPointService;

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
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully post request", content = @Content(mediaType = "application/json",array = @ArraySchema(schema = @Schema(implementation = BulkUserStarPointsDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Request is not authorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content)})
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BulkUserStarPointsDTO> getStarPointsByUsers(final @RequestBody RequestStarPointsDTO requestStarPointsDTO) {
        return starPointService.getStarPointsByMultipleUsers(requestStarPointsDTO);
    }
}
