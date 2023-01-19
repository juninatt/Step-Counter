package se.sigma.boostapp.boost_app_java.util;

import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Sorter {
    private static final Comparator<StepDTO> endTimeComparator = Comparator.comparing(StepDTO::getEndTime);

    public List<StepDTO> sortStepDTOListByEndTime(List<StepDTO> stepDtoList) {
        if (isNull(stepDtoList)) {
            return new ArrayList<>();
        }
        return stepDtoList.stream()
                .sorted(endTimeComparator)
                .collect(Collectors.toList());
    }

    public StepDTO getOldestDTOFromList(List<StepDTO> stepDtoList) {
        if (isNull(stepDtoList)) {
            return createDefaultStepDTO();
        }
        return stepDtoList.stream()
                .min(endTimeComparator)
                .orElseGet(this::createDefaultStepDTO);
    }

    public List<StepDTO> collectStepDTOsWhereEndTimeIsAfter(List<StepDTO> dtoList, LocalDateTime date) {
        if (isNull(dtoList, date)) {
            return new ArrayList<>();
        }
        return dtoList.stream()
                .filter(stepDTO -> stepDTO.getEndTime().isAfter(date))
                .collect(Collectors.toList());
    }

    private StepDTO createDefaultStepDTO() {
        LocalDateTime now = LocalDateTime.now();
        return new StepDTO(0, now, now, now);
    }

    private boolean isNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }
}

