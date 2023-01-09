package se.sigma.boostapp.boost_app_java.converter.implementation;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.MonthStep;

public class StepDTOtoMonthStepConverter implements BoostAppConverter<StepDTO, MonthStep> {

    private final String userID;

    public StepDTOtoMonthStepConverter(String userID) {
        this.userID = userID;
    }

    @Override
    public MonthStep convert(StepDTO from) {
        var month = from.getEndTime().getMonthValue();
        var year = from.getEndTime().getYear();
        var stepCount = from.getStepCount();
        return new MonthStep(userID, month, year, stepCount);
    }
}
