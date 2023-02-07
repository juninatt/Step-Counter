package com.nexergroup.boostapp.java.step.util;


import org.junit.jupiter.api.*;
import com.nexergroup.boostapp.java.step.dto.stepdto.StepDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@DisplayName(" <== StepDtoSorter Tests ==>")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StepDtoSorterTest {
    StepDtoSorter sorter;
    List<StepDTO> sortedDTOList;
    List<StepDTO> unsortedDTOList;
    List<StepDTO> testDTOList;

    @BeforeAll
    public void init() {
        sorter = new StepDtoSorter();

        LocalDateTime morningTime = LocalDateTime.of(2022, 1, 1, 8, 1);
        LocalDateTime lunchTime = LocalDateTime.of(2022, 1, 1, 12, 1);
        LocalDateTime dinnerTime = LocalDateTime.of(2022, 1, 1, 18, 1);

        StepDTO morningTestDTO = new StepDTO(0, morningTime, morningTime, morningTime);
        StepDTO lunchTestDTO = new StepDTO(0, lunchTime, lunchTime, lunchTime);
        StepDTO eveningTestDTO = new StepDTO(0, dinnerTime, dinnerTime, dinnerTime);

        sortedDTOList = new ArrayList<>(List.of(morningTestDTO, lunchTestDTO, eveningTestDTO));
        unsortedDTOList = new ArrayList<>(List.of(eveningTestDTO, morningTestDTO, lunchTestDTO));
    }


    @Test
    @DisplayName("sortByEndTime should return a list of StepDTO-objects in ascending order by endTime ")
    void testSortByEndTime_ReturnsSortedList() {
        assertThat(testDTOList, not(equalTo(sortedDTOList)));
        testDTOList = sorter.sortByEndTime(unsortedDTOList);
        Assertions.assertArrayEquals(testDTOList.toArray(), sortedDTOList.toArray());
    }


    @Test
    @DisplayName("getOldest should return DTO with oldest end-time")
    void testGetOldest_ReturnsOldestDto() {
        StepDTO testDTO = sorter.getOldest(sortedDTOList);
        assertThat(testDTO.getEndTime(), is(LocalDateTime.of(2022, 1, 1, 8, 1)));
    }


    @Test
    @DisplayName("collectEndTimeIsAfter should return a list of objects where endTime is after date-input")
    void testCollectEndTimeIsAfter_ReturnsFilteredList() {
        LocalDateTime endTime = LocalDateTime.of(2022, 1, 1, 8, 1, 1);
        var expectedResult = List.of(sortedDTOList.get(1), sortedDTOList.get(2));
        testDTOList = sorter.collectEndTimeIsAfter(sortedDTOList, endTime);
        assertThat(testDTOList, is(expectedResult));
    }
}
