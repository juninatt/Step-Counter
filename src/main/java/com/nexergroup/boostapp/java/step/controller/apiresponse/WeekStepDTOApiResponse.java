package com.nexergroup.boostapp.java.step.controller.apiresponse;

import com.nexergroup.boostapp.java.step.dto.stepdto.WeekStepDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@GroupedApiResponse
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successful GET request",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = WeekStepDTO.class))
        )
})
public @interface WeekStepDTOApiResponse {
}
