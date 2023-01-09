package se.sigma.boostapp.boost_app_java.util;


import org.junit.jupiter.api.*;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@DisplayName("Class Sorter tests:")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SorterTest {
    Sorter sorter;
    List<StepDTO> sortedDTOList;
    List<StepDTO> unsortedDTOList;
    List<StepDTO> testDTOList;

    private void setUpTestData() {
        LocalDateTime morningTime = LocalDateTime.of(2022, 1, 1, 8, 1);
        LocalDateTime lunchTime = LocalDateTime.of(2022, 1, 1, 12, 1);
        LocalDateTime dinnerTime = LocalDateTime.of(2022, 1, 1, 18, 1);

        StepDTO morningTestDTO = new StepDTO(0, morningTime, morningTime, morningTime);
        StepDTO lunchTestDTO = new StepDTO(0, lunchTime, lunchTime, lunchTime);
        StepDTO eveningTestDTO = new StepDTO( 0, dinnerTime, dinnerTime, dinnerTime);

        sortedDTOList = new ArrayList<>(List.of(morningTestDTO, lunchTestDTO, eveningTestDTO));
        unsortedDTOList = new ArrayList<>(List.of(eveningTestDTO, morningTestDTO, lunchTestDTO));
    }

    @BeforeAll
    public void init() {
        sorter = new Sorter();
    }
    @BeforeEach
    public void setUp() {
        setUpTestData();
    }

    @Nested
    @DisplayName("SortStepDTOListByEndTime should ")
    class getOldestDTOFromListTest {

        @Test
        @DisplayName("return an empty list when list-parameter is null")
        void shouldReturnEmptyListWhenPassedANullValue() {
            testDTOList = sorter.sortStepDTOListByEndTime(null);
            assertThat(testDTOList.size(), is(0));
        }


        @Test
        @DisplayName("return a list of StepDTO-objects in ascending order by endTime ")
        void shouldReturnSortedList() {
            assertThat(testDTOList, not(equalTo(sortedDTOList)));
            testDTOList = sorter.sortStepDTOListByEndTime(unsortedDTOList);
            Assertions.assertArrayEquals(testDTOList.toArray(), sortedDTOList.toArray());
        }

        @Nested
        @DisplayName("GetOldestDTOFromList-method should ")
        class GetOldestDTOFromListTest {
            StepDTO testDTO;

            @Test
            @DisplayName("return DTO with oldest end-time")
            void shouldReturnOldestDTO() {
                testDTO = sorter.getOldestDTOFromList(sortedDTOList);
                assertThat(testDTO.getEndTime(), is(LocalDateTime.of(2022, 1, 1, 8, 1)));
            }

            @Test
            @DisplayName("return DTO with a step count of zero when list passed to method is null")
            void shouldReturnErrorDTOIfNullValueIsPassedToMethod() {
                testDTO = sorter.getOldestDTOFromList(null);
                assertThat(testDTO.getStepCount(), is(0));
            }
        }
    }
    @Nested
    @DisplayName("CollectStepDTOsWhereEndTimeIsAfter-method should ")
    class collectStepDTOsWhereEndTimeIsAfterTest {
        LocalDateTime endTime = LocalDateTime.of(2022, 1, 1, 8, 1, 1);

        @Test
        @DisplayName("return an empty list when list-parameter is null")
        void shouldReturnEmptyListWhenPassedNullList() {
            var expectedResult = 0;
            testDTOList = sorter.collectStepDTOsWhereEndTimeIsAfter(null, endTime);
            assertThat(expectedResult, is(testDTOList.size()));
        }

        @Test
        @DisplayName("return original list when date-parameter is null")
        void shouldReturnOriginalListIfDateIsNull() {
            var expectedResult = sortedDTOList;
            testDTOList = sorter.collectStepDTOsWhereEndTimeIsAfter(sortedDTOList, null);
            assertThat(sortedDTOList, is(expectedResult));
        }

        @Test
        @DisplayName("return a list of objects where endTime is after date-parameter")
        void shouldReturnListWithEndTimeAfterDate() {
            var expectedResult = List.of(sortedDTOList.get(1), sortedDTOList.get(2));
            testDTOList = sorter.collectStepDTOsWhereEndTimeIsAfter(sortedDTOList, endTime);
            assertThat(testDTOList, is(expectedResult));
        }
    }
}