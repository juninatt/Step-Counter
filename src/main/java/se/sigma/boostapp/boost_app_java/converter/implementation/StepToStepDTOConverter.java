package se.sigma.boostapp.boost_app_java.converter.implementation;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

public class StepToStepDTOConverter implements BoostAppConverter<Step, StepDTO> {

    @Override
    public StepDTO convert(Step from) {
        var stepCount = from.getStepCount();
        var startTime = from.getStartTime();
        var endTime = from.getEndTime();
        var uploadTime = from.getUploadedTime();
        return new StepDTO(stepCount, startTime, endTime, uploadTime);
    }
}
