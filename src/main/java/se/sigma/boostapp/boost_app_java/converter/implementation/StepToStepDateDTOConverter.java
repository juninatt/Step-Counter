package se.sigma.boostapp.boost_app_java.converter.implementation;

import se.sigma.boostapp.boost_app_java.converter.BoostAppConverter;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDateDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.util.parser.LocalDateTimeToUtilDateParser;
import se.sigma.boostapp.boost_app_java.util.Matcher;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Date;

public class StepToStepDateDTOConverter implements BoostAppConverter<Step, StepDateDTO> {

    private final String userID;
    private final Matcher matcher;

    private final StepDateDTO defaultDTO;

    public StepToStepDateDTOConverter(String userID, Matcher matcher) {
        this.userID = userID;
        this.matcher = matcher;
        this.defaultDTO = new StepDateDTO(userID, 0);
    }

    @Override
    public StepDateDTO convert(Step from) {
        try {
            LocalDateTime endTime = from.getEndTime();
            Date date = toDate(endTime);
            int dayOfWeek = matcher.getDayOfWeekFromDate(date);
            return new StepDateDTO(userID, date, dayOfWeek, from.getStepCount());
        } catch (NullPointerException | IllegalArgumentException | DateTimeException exception) {
            System.out.println(exception.getMessage());
            return defaultDTO;
        } catch (Exception exception) {
            exception.printStackTrace();
            return defaultDTO;
    }
}
    private Date toDate(LocalDateTime localDateTime) {
        var parser = new LocalDateTimeToUtilDateParser();
        return parser.convert(localDateTime);
    }
}


