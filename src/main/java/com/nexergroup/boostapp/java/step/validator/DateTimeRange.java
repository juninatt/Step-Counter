package com.nexergroup.boostapp.java.step.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = DateTimeRangeValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimeRange {
    String message() default "Start time must before end time, which in turn must be before uploaded time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
