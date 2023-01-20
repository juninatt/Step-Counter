package se.sigma.boostapp.boost_app_java.util;

import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for sorting and filtering StepDTO objects.
 *
 * @see StepDTO
 */
public class StepDtoSorter {
    private static final Comparator<StepDTO> endTimeComparator = Comparator.comparing(StepDTO::getEndTime);

    /**
     * Sorts the provided list of StepDTO objects by their end time.
     *
     * @param stepDtoList the list of StepDTO objects to sort
     * @return the sorted list of StepDTO objects
     */
    public List<StepDTO> sortByEndTime(@NotNull List<StepDTO> stepDtoList) {
        return stepDtoList.stream()
                .sorted(endTimeComparator)
                .collect(Collectors.toList());
    }

    /**
     * Returns the oldest StepDTO object in the provided list, based on its end time.
     *
     * @param stepDtoList the list of StepDTO objects to search
     * @return the oldest StepDTO object in the list
     */
    public StepDTO getOldest(@NotNull List<StepDTO> stepDtoList) {
        return stepDtoList.stream()
                .min(endTimeComparator)
                .orElseThrow();
    }

    /**
     * Filters the provided list of StepDTO objects by their end time,
     * returning only those objects whose end time is after the provided time.
     *
     * @param dtoList the list of StepDTO objects to filter
     * @param time the time to compare against
     * @return the filtered list of StepDTO objects
     */
    public List<StepDTO> collectEndTimeIsAfter(@NotNull List<StepDTO> dtoList, @NotNull LocalDateTime time) {
        return Optional.ofNullable(dtoList).orElse(Collections.emptyList()).stream()
                .filter(stepDTO -> stepDTO.getEndTime().isAfter(time))
                .collect(Collectors.toList());
    }
}
