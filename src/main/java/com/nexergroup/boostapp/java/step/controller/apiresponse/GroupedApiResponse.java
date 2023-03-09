package com.nexergroup.boostapp.java.step.controller.apiresponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a Java annotation that can be used to group multiple {@link ApiResponse} annotations
 * together. It is intended to be used on methods in a controller class to specify the possible
 * responses that the method can return.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Request is not authorized. Check authentication credentials.\n" +
                        "See Boost App wiki documentation for generating JWT through Postman",
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
                responseCode = "503",
                description = "Service unavailable. Server not ready to handle request. See Azure Portal for troubleshooting",
                content = @Content)
})
public @interface GroupedApiResponse {
}
