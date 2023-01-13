package se.sigma.boostapp.boost_app_java.util;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Component
public class Matcher {
    public List<String> getMatchingStrings(List<String> listA, List<String> listB) {
        if (listA == null || listB == null) {
            return Collections.emptyList();
        }
        Set<String> setA = new HashSet<>(listA);
        return listB.stream()
                .filter(setA::contains)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int getWeekNumberFromDate(LocalDateTime localDateTime)  {
        if (localDateTime == null) {
            return 0;
        }
        LocalDate date = LocalDate.ofYearDay(localDateTime.getYear(), localDateTime.getDayOfYear());
        int weekNumber = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return Math.min(weekNumber, 52);
    }

    public int getDayOfWeekFromDate(Date utilDate) {
        LocalDate localDate = null;
        try {
            localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        catch (NullPointerException exception) {
            System.out.println("Error receiving day of week: " + exception.getMessage());
        }
        switch (Objects.requireNonNull(localDate).getDayOfWeek()) {
            case SUNDAY: return 1;
            case MONDAY: return 2;
            case TUESDAY: return 3;
            case WEDNESDAY: return 4;
            case THURSDAY: return 5;
            case FRIDAY: return 6;
            case SATURDAY: return 7;
            default: throw new IllegalStateException("Unexpected value: " + localDate.getDayOfWeek());
        }
    }

    public boolean endTimeIsSameYear(StepDTO stepDTO, Step step) {
        boolean isSameYear;
        try {
            isSameYear = checkEndTime(stepDTO, step, LocalDateTime::getYear);
        } catch (NullPointerException | DateTimeException exception) {
            System.out.println("Error comparing if endTime is on same year: " + exception.getMessage());
            isSameYear = false;
        }
        return isSameYear;
    }

    public boolean endTimeIsSameDay(@NotNull StepDTO stepDTO,@NotNull Step step) {
        boolean isSameDay;
        try {
            isSameDay = checkEndTime(stepDTO, step, LocalDateTime::getDayOfYear)
                    && stepDTO.getYear() == step.getEndTime().getYear();

        } catch (NullPointerException | DataAccessException exception) {
            System.out.println("Error comparing if endTime is on same day: " + exception.getMessage());
            isSameDay = false;
        }
        return isSameDay;
    }

    private boolean checkEndTime(StepDTO stepDTO, Step step, ToIntFunction<LocalDateTime> getter) {
        try {
            return getter.applyAsInt(step.getEndTime()) == getter.applyAsInt(stepDTO.getEndTime());
        } catch (DateTimeException | NullPointerException exception) {
            System.out.println("Error when checking endTime: " + exception.getMessage());
            return false;
        }
    }

    public boolean shouldCreateNewStep(@NotNull StepDTO stepDTO,@NotNull Step existingStep) {
            return existingStep.getStepCount() == 0 || !dtoEndedBeforeStep(stepDTO, existingStep);
    }

    public boolean dtoEndedBeforeStep(@NotNull StepDTO stepDto,@NotNull Step currentStep) {
        return currentStep.getEndTime().isBefore(stepDto.getEndTime());
    }
}

