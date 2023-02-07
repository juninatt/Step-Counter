package com.nexergroup.boostapp.java.step.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DisplayName(" <== DateHelper Tests ==>")
public class DateHelperTest {

    LocalDateTime firstMinuteOfYear;
    LocalDateTime lastMinuteOfYear;
    int weekNumber;
    @BeforeEach
    void init() {
        firstMinuteOfYear = LocalDateTime.of(2022, 1, 1, 1, 1);
        lastMinuteOfYear = LocalDateTime.of(2022, 12, 31, 23, 59);
    }

    @Test
    @DisplayName("getWeek should return 1 when input is first minute of the year")
    public void testGetWeek_Returns1ForYearsFirstMinute() {
        weekNumber = DateHelper.getWeek(firstMinuteOfYear);
        assertThat(weekNumber, is(1));
    }

    @Test
    @DisplayName("getWeek should return 52 when input is last minute of the year")
    public void testGetWeek_Returns52ForYearsLastMinute() {
        weekNumber = DateHelper.getWeek(lastMinuteOfYear);
        assertThat(weekNumber, is(52));
    }

    @Test
    @DisplayName("getWeek should return 0 when input is null")
    public void testGetWeek_Returns0WhenInputIsNull() {
        weekNumber = DateHelper.getWeek(null);
        assertThat(weekNumber, is(0));
    }

    @Test
    @DisplayName("getWeek should handle leap year correctly")
    public void testGetWeek_leapYear() {
        LocalDateTime time = LocalDateTime.of(2020, 2, 29, 12, 0);
        int weekNumber = DateHelper.getWeek(time);
        assertThat(weekNumber, is(9));
    }
}
