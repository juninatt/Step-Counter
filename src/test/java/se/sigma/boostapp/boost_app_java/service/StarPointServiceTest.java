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

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class) saknas dependency? slipper init mocks i
public class StarPointServiceTest {

    @Mock
    private StepRepository mockedStepRepository;

    @InjectMocks
    private StarPointService starPointServiceTest;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        starPointServiceTest = new StarPointService(mockedStepRepository);
    }

    @Test
    public void testNullUsers_CallsGetAllUsers() {

        List<String> emptyList = null;
        starPointServiceTest.getStarPointsByMultipleUsers(new RequestStarPointsDTO(emptyList, LocalDateTime.parse("2021-08-21T01:00:00"), LocalDateTime.parse("2021-08-22T01:00:00")));
        verify(mockedStepRepository).getAllUsers();
    }

        /*
    Mina tankar:

    Setup:
    *Fake databas - lista med några användare

    Jag vill testa:
     - Om RequestStarpointDto inte har medskickad lista att den fylls med alla - Verify att stepRepositpry.getAllUsers kallas?
     - Om starttime och eller endtime saknas - svar tom lista
     - om userid inte finns - svar tom lista
     - om userid finns samt tidsintervall finns - Rätt storlek på lista tillbaka? Rätt steg (samma som starpoints)
     */
}