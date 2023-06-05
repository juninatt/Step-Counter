package se.pbt.stepcounter.controller.apiresponse;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@GroupedApiResponse
@ApiResponse(
        responseCode = "200",
        description = "Successful GET request"
)
public @interface OkGetRequest {
        Class<?> schemaImplementation() default Void.class;
        String mediaType() default "application/json";
}