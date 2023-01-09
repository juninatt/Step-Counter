package se.sigma.boostapp.boost_app_java.controller.apiresponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a Java annotation that can be used to group multiple {@see ApiResponse} annotations
 * together. It is intended to be used on methods in a controller class to specify the possible
 * responses that the method can return.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful post request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Step.class))),
        @ApiResponse(responseCode = "401", description = "Request is not authorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accessing the resource is forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content),
})
public @interface GroupedApiResponse {
}