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
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successful GET request",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = WeekStepDTO.class))),
        @ApiResponse(
                responseCode = "401",
                description = "Request is not authorized. Check authentication credentials.",
                content = @Content),
        @ApiResponse(
                responseCode = "403",
                description = "Request is forbidden. Caller not authorized.",
                content = @Content),
        @ApiResponse(
                responseCode = "404",
                description = "Requested resource can not be found.",
                content = @Content),
        @ApiResponse(
                responseCode = "500",
                description = "Internal Server error. Unexpected condition prevented request.",
                content = @Content),
        @ApiResponse(
                responseCode = "Service unavailable. Server not ready to handle request.",
                content = @Content)
})
public @interface WeekStepDTOApiResponse {
}
