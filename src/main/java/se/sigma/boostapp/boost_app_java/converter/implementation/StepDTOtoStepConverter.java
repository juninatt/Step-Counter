package se.sigma.boostapp.boost_app_java.converter.implementation;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

public class StepDTOtoStepConverter implements BoostAppConverter<StepDTO, Step> {

    private final String userID;

    public StepDTOtoStepConverter(String userID) {
        this.userID = userID;
    }

    @Override
    public Step convert(StepDTO from) {
        var stepCount = from.getStepCount();
        var startTime = from.getStartTime();
        var endTime = from.getEndTime();
        var uploadTime = from.getUploadTime();
        return new Step(userID, stepCount, startTime, endTime, uploadTime);
    }
}
