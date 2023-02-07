package com.nexergroup.boostapp.java.step.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

@DisplayName(" <== StringComparator Tests ==>")
public class StringComparatorTest {

    private Set<String> matchingStrings;
    private List<String> listA;
    private List<String> listB;


    @BeforeEach
    void init() {
        matchingStrings = new HashSet<>();
        listA = new ArrayList<>(List.of("A", "B", "C"));
        listB = new ArrayList<>(List.of("C", "B", "A", "ERROR", "E"));
    }

    @Test
    @DisplayName("getMatching should return an empty list when input to first parameter is null")
    void testGetMatching_WhenFirstParameterIsNull_ReturnsEmptyList() {
        matchingStrings  = StringComparator.getMatching(listA, null);
        assertThat(matchingStrings.size(), is(0));
    }

    @Test
    @DisplayName("getMatching should return an empty list when input to second parameter is null")
    void testGetMatching_WhenSecondParameterIsNull_ReturnsEmptyList() {
        matchingStrings  = StringComparator.getMatching(listA, null);
        assertThat(matchingStrings.size(), is(0));
    }

    @Test
    @DisplayName("getMatching should return an empty list when input to both parameters is null")
    void testGetMatching_WhenBothParametersAreNull_ReturnsEmptyList() {
        matchingStrings  = StringComparator.getMatching(null, null);
        assertThat(matchingStrings.size(), is(0));
    }

    @Test
    @DisplayName("getMatching should return a list with correct length when input is valid")
    void testGetMatching_WhenInputIsValid_ReturnsListOfCorrectLength() {
        int numberOfMatchingStrings = StringComparator.getMatching(listA, listB).size();
        assertThat(numberOfMatchingStrings, is(3));
    }

    @Test
    @DisplayName("returned set should contain the correct values")
    void testGetMatching_ReturnsCorrectValues() {
        matchingStrings = StringComparator.getMatching(listA, listB);
        assertThat(matchingStrings, containsInAnyOrder("A", "B", "C"));
    }

    @Test
    @DisplayName("returned set should be immutable")
    void testGetMatching_ReturnsImmutableSet() {
        matchingStrings = StringComparator.getMatching(listA, listB);
        try {
            matchingStrings.add("Z");
            Assertions.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException exception) {
            // expected
        }
        assertThat(matchingStrings, containsInAnyOrder("A", "B", "C"));
    }
}
