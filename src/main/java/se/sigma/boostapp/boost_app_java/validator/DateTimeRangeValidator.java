package se.sigma.boostapp.boost_app_java.validator;

import se.sigma.boostapp.boost_app_java.dto.StepDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author SigmaIT
 *
 */
public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, StepDTO> {
	
/**
 * @author SigmaIT
 */
    @Override
    public void initialize(DateTimeRange constraintAnnotation) {
    }
    
/**
 * @author SigmaIT
 */
    @Override
    public boolean isValid(StepDTO value, ConstraintValidatorContext context) {
        return !value.getEndTime().isAfter(value.getUploadedTime())
                && !value.getStartTime().isAfter(value.getEndTime());
    }
}
