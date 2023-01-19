package se.sigma.boostapp.boost_app_java.util;

import org.junit.jupiter.api.*;
import se.sigma.boostapp.boost_app_java.dto.stepdto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DisplayName("Matcher tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MatcherTest {

    Matcher matcher;

    @BeforeAll
    void init() {
        matcher = new Matcher();
    }

    @Nested
    @DisplayName("GetMatchingStrings-method should ")
    class GetMatchingStringsFromListTest {
        List<String> matchingStrings;
        List<String> listA;
        List<String> listB;


        @BeforeEach
        void init() {
            matchingStrings = new ArrayList<>();
            listA = new ArrayList<>(List.of("A", "B", "C"));
            listB = new ArrayList<>(List.of("C", "B", "A", "ERROR", "E"));
        }

        @Test
        @DisplayName("return an empty list when first list-parameter is null")
        void emptyListShouldBeReturnedIfFirstParameterIsNull() {
            matchingStrings  = matcher.getMatchingStrings(listA, null);
            assertThat(matchingStrings.size(), is(0));
        }

        @Test
        @DisplayName("return an empty list when second list-parameter is null")
        void emptyListShouldBeReturnedIfSecondParameterIsNull() {
            matchingStrings  = matcher.getMatchingStrings(listA, null);
            assertThat(matchingStrings.size(), is(0));
        }
        @Test
        @DisplayName("return a list with correct length")
        void lengthOfListOfMatchingUsersShouldBeCorrect() {
            int numberOfMatchingStrings = matcher.getMatchingStrings(listA, listB).size();
            assertThat(numberOfMatchingStrings, is(3));
        }
    }

    @Nested
    @DisplayName("EndTimeIsSameYear-method should return ")
    public class EndTimeIsSameYearTest {
        protected Step testStepDateA;
        protected Step testStepDateB;
        protected StepDTO testDTODateA;
        protected boolean isSameYear;

        @BeforeEach
        void init() {
            LocalDateTime oneOClockFirstJan21 = LocalDateTime.of(2021, 1, 1, 1, 0, 0);
            LocalDateTime oneOClockFirstJan22 = LocalDateTime.of(2022, 1, 1, 1, 0, 0);

            testStepDateA = new Step("testUser",69, oneOClockFirstJan21, oneOClockFirstJan21, oneOClockFirstJan21);
            testDTODateA =  new StepDTO(13,oneOClockFirstJan22, oneOClockFirstJan21, oneOClockFirstJan21);
            testStepDateB = new Step("secondTestUser",1, oneOClockFirstJan22, oneOClockFirstJan22, oneOClockFirstJan22);
        }

        @Test
        @DisplayName("false when StepDTO-parameter is null")
        public void nullStepDTOShouldReturnFalse() {
            isSameYear = true;
            isSameYear = matcher.endTimeIsSameYear(null, testStepDateA);
            assertThat(isSameYear, is(false));
        }

        @Test
        @DisplayName("false when Step-parameter is null")
        public void nullStepShouldReturnFalse() {
            isSameYear = true;
            isSameYear = matcher.endTimeIsSameYear(testDTODateA, null);
            assertThat(isSameYear, is(false));
        }

        @Test
        @DisplayName("false when dates are on different years")
        public void datesFromDifferentYearsShouldReturnFalse() {
            isSameYear = true;
            isSameYear = matcher.endTimeIsSameYear(testDTODateA, testStepDateB);
            assertThat(isSameYear, is(false));
        }

        @Test
        @DisplayName("true when dates same year")
        public void datesFromSameYearsShouldReturnTrue() {
            isSameYear = false;
            isSameYear = matcher.endTimeIsSameYear(testDTODateA, testStepDateA);
            assertThat(isSameYear, is(true));
        }

        @Nested
        @DisplayName("EndTimeIsSameDay-method should return ")
        class EndTimeIsSameDayTest {
            boolean isSameDay;

            @BeforeEach
            void init() {
            }

            @Test
            @DisplayName("false when StepDTO-parameter is null")
            public void nullStepDTOShouldReturnFalse() {
                isSameDay = true;
                isSameDay = matcher.endTimeIsSameDay(null, testStepDateA);
                assertThat(isSameDay, is(false));
            }

            @Test
            @DisplayName("false when Step-parameter is null")
            public void nullStepShouldReturnFalse() {
                isSameDay = true;
                isSameDay = matcher.endTimeIsSameDay(testDTODateA, null);
                assertThat(isSameDay, is(false));
            }

            @Test
            @DisplayName("false when dates are on same day buy different years")
            public void datesFromSameDayOnDifferentYearsShouldReturnFalse() {
                isSameDay = true;
                isSameDay = matcher.endTimeIsSameDay(testDTODateA, testStepDateB);
                assertThat(isSameDay, is(false));
            }

            @Test
            @DisplayName("true when dates are on same day and year")
            public void datesFromSameDayOnSameYearShouldReturnTrue() {
                isSameDay = false;
                isSameDay = matcher.endTimeIsSameDay(testDTODateA, testStepDateA);
                assertThat(isSameDay, is(true));
            }
        }
    }
}
