package se.sigma.boostapp.boost_app_java.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import se.sigma.boostapp.boost_app_java.dto.RequestStarPointsDTO;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class) saknas dependency? slipper init mocks i
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
        verify(mockedStepRepository).getAllUsers();
    }

    @Test
    public void testNoStartOrEndTime_ReturnsEmptyList() {
        List<String> users = List.of("1", "2");
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO());
    }

    @Test
    public void testOneUserWithCorrectData_ReturnsCorrectSizeListAndContent() {
        List<String> users = new ArrayList<>(List.of("1"));
        RequestStarPointsDTO correctData = new RequestStarPointsDTO(users, STARTTIME, ENDTIME);

        when(mockedStepRepository.getStepCountSum("1", STARTTIME, ENDTIME)).thenReturn(Optional.of(10));

        var bulkUsers = starPointServiceTest.getStarPointsByMultipleUsers(correctData);
        assertEquals(1, bulkUsers.size());
        assertEquals(10, bulkUsers.get(0).getStarPoints().getStarPoints());
    }

    @Test
    public void testTwoUsersWithCorrectData_ReturnsCorrectSizeListAndContent() {
        List<String> users = new ArrayList<>(List.of("1", "2"));
        RequestStarPointsDTO correctData = new RequestStarPointsDTO(users, STARTTIME, ENDTIME);

        when(mockedStepRepository.getStepCountSum("1", STARTTIME, ENDTIME)).thenReturn(Optional.of(10));
        when(mockedStepRepository.getStepCountSum("2", STARTTIME, ENDTIME)).thenReturn(Optional.of(20));

        var bulkUsers = starPointServiceTest.getStarPointsByMultipleUsers(correctData);
        assertEquals(2, bulkUsers.size());
        assertEquals(20, bulkUsers.get(1).getStarPoints().getStarPoints());
        assertNotEquals(bulkUsers.get(0).getStarPoints().getStarPoints(), bulkUsers.get(1).getStarPoints().getStarPoints());
    }

        /*
    Våra tankar:

    Setup:
    *Fake databas - lista med några användare

    Jag vill testa:
     - Om RequestStarpointDto inte har medskickad lista att den fylls med alla - Verify att stepRepositpry.getAllUsers kallas?
     - Om starttime och eller endtime saknas - svar tom lista
     - om userid inte finns - svar tom lista
     - om userid finns samt tidsintervall finns - Rätt storlek på lista tillbaka? Rätt steg (samma som starpoints)
     */
}