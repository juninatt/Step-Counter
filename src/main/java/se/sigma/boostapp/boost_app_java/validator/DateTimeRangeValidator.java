package se.sigma.boostapp.boost_app_java.validator;

import se.sigma.boostapp.boost_app_java.dto.StepDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, StepDTO> {

    @Override
    public void initialize(DateTimeRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(StepDTO value, ConstraintValidatorContext context) {
        return !value.getEndTime().isAfter(value.getUploadedTime())
                && !value.getStartTime().isAfter(value.getEndTime());
    }
}
