package se.pbt.stepcounter.validator;

import se.pbt.stepcounter.dto.stepdto.StepDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, StepDTO> {


    @Override
    public void initialize(DateTimeRange constraintAnnotation) {
    }


    @Override
    public boolean isValid(StepDTO value, ConstraintValidatorContext context) {
        return !value.getEndTime().isAfter(value.getUploadTime())
                && !value.getStartTime().isAfter(value.getEndTime())
                && !value.getEndTime().isAfter(value.getUploadTime())
                && !value.getStartTime().isEqual(value.getEndTime())
                && !value.getStartTime().isAfter(value.getUploadTime());
    }
}
