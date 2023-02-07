package com.nexergroup.boostapp.java.step.util;

import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for sorting and filtering {@link StepDTO} objects.
 * The class provides methods to sort a list of StepDTO objects based on their end time,
 * to retrieve the oldest StepDTO object in the list, and to filter the list by end time.
 *
 * @see Comparator
 */
public class StepDtoSorter {
    private static final Comparator<StepDTO> endTimeComparator = Comparator.comparing(StepDTO::getEndTime);

    /**
     * Sorts the provided list of {@link StepDTO} objects by their end time.
     *
     * @param stepDtoList the list of {@link StepDTO} objects to sort
     * @return the sorted list of {@link StepDTO} objects
     */
    public List<StepDTO> sortByEndTime(@NotNull List<StepDTO> stepDtoList) {
        return stepDtoList.stream()
                .sorted(endTimeComparator)
                .collect(Collectors.toList());
    }

    /**
     * Returns the oldest {@link StepDTO} object in the provided list, based on its end time.
     *
     * @param stepDtoList the list of {@link StepDTO} objects to search
     * @return the oldest {@link StepDTO} object in the list
     */
    public StepDTO getOldest(@NotNull List<StepDTO> stepDtoList) {
        return stepDtoList.stream()
                .min(endTimeComparator)
                .orElseThrow();
    }

    /**
     * Filters the provided list of {@link StepDTO} objects by their end time,
     * returning only those objects whose end time is after the provided time.
     *
     * @param dtoList the list of {@link StepDTO} objects to filter
     * @param time LocalDateTime object to compare against
     * @return the filtered list of {@link StepDTO} objects
     */
    public List<StepDTO> collectEndTimeIsAfter(@NotNull List<StepDTO> dtoList, @NotNull LocalDateTime time) {
        return Optional.ofNullable(dtoList).orElse(Collections.emptyList()).stream()
                .filter(stepDTO -> stepDTO.getEndTime().isAfter(time))
                .collect(Collectors.toList());
    }
}
