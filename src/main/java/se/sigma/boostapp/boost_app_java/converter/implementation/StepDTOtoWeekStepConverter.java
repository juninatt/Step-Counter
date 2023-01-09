package se.sigma.boostapp.boost_app_java.converter.implementation;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.WeekStep;
import se.sigma.boostapp.boost_app_java.util.Matcher;

public class StepDTOtoWeekStepConverter implements BoostAppConverter<StepDTO, WeekStep> {

    private final String userID;

    private final Matcher matcher;

    public StepDTOtoWeekStepConverter(String userID) {
        this.userID = userID;
        matcher = new Matcher();
    }

    @Override
    public WeekStep convert(StepDTO from) {
        var week = matcher.getWeekNumberFromDate(from.getEndTime());
        var year = from.getEndTime().getYear();
        var stepCount = from.getStepCount();
        return new WeekStep(userID, week, year, stepCount);
    }
}
