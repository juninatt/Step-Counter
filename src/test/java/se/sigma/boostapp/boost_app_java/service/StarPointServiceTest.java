package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import se.sigma.boostapp.boost_app_java.dto.starpointdto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StarPointServiceTest {

    @Mock
    private StepRepository mockedStepRepository;

    @InjectMocks
    private StarPointService starPointServiceTest;

    private final LocalDateTime STARTTIME = LocalDateTime.parse("2021-08-21T01:00:00");
    private final LocalDateTime ENDTIME = LocalDateTime.parse("2021-08-22T01:00:00");

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        starPointServiceTest = new StarPointService(mockedStepRepository);
    }

    @Test
    public void testNullUsers_CallsGetAllUsers() {
        List<String> emptyList = null;
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(emptyList, STARTTIME, ENDTIME));
        verify(mockedStepRepository).getListOfAllDistinctUserId();
    }

    @Test
    public void testEmptyUsers_CallsGetAllUsers() {
        List<String> emptyList = new ArrayList<>();
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(emptyList, STARTTIME, ENDTIME));
        verify(mockedStepRepository).getListOfAllDistinctUserId();
    }

    @Test(expected = DateTimeParseException.class)
    public void testEmptyStartAndEndLDT_throwsParseExc() {
        List<String> users = List.of("1", "2");
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(users, LocalDateTime.parse(""), LocalDateTime.parse("")));
    }

    @Test
    public void testNullStartAndEndLDT_returnsEmptyList() {
        List<String> users = List.of("1", "2");
        var result = starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(users, null, null));
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test(expected = NullPointerException.class)
    public void testNullStartAndEndLDTWithBadMock_throwsNull() {
        List<String> users = List.of("1", "2");
        when(mockedStepRepository.getStepCountByUserIdAndDateRange("1", null, null)).thenReturn(Optional.of(10));
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(users, null, null));
    }

    @Test
    public void testOneUserWithCorrectData_ReturnsCorrectSizeListAndContent() {
        List<String> users = new ArrayList<>(List.of("1"));
        RequestStarPointsDTO correctData = new RequestStarPointsDTO(users, STARTTIME, ENDTIME);

        when(mockedStepRepository.getStepCountByUserIdAndDateRange("1", STARTTIME, ENDTIME)).thenReturn(Optional.of(10));

        var bulkUsers = starPointServiceTest.getStarPointsByMultipleUsers(correctData);
        assertEquals(1, bulkUsers.size());
        assertEquals(10, bulkUsers.get(0).getStarPoints().getStarPoints());
    }

    @Test
    public void testTwoUsersWithCorrectData_ReturnsCorrectSizeListAndContent() {
        List<String> users = new ArrayList<>(List.of("1", "2"));
        RequestStarPointsDTO correctData = new RequestStarPointsDTO(users, STARTTIME, ENDTIME);

        when(mockedStepRepository.getStepCountByUserIdAndDateRange("1", STARTTIME, ENDTIME)).thenReturn(Optional.of(10));
        when(mockedStepRepository.getStepCountByUserIdAndDateRange("2", STARTTIME, ENDTIME)).thenReturn(Optional.of(20));

        var bulkUsers = starPointServiceTest.getStarPointsByMultipleUsers(correctData);
        assertEquals(2, bulkUsers.size());
        var firstUserPoints = bulkUsers.get(0).getStarPoints().getStarPoints();
        var secondUserPoints = bulkUsers.get(1).getStarPoints().getStarPoints();
        assertEquals(20, secondUserPoints);
        assertNotEquals(firstUserPoints, secondUserPoints);
    }
}