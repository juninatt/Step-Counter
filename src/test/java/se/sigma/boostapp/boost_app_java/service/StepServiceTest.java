package se.sigma.boostapp.boost_app_java.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import se.sigma.boostapp.boost_app_java.dto.StepDTO;
import se.sigma.boostapp.boost_app_java.model.Step;
import se.sigma.boostapp.boost_app_java.repository.StepRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StepServiceTest {

    @Mock
    private StepRepository mockedStepRepository;
    @InjectMocks
    private StepService stepService;
    private Step step;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        stepService = new StepService(mockedStepRepository);
        step = new Step();
    }

    @Test
    public void registerStepsTest() {
       final StepDTO stepDTO = new StepDTO(100,
                LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

       //användare finns inte i databas
       assertNull(step.getUserId());

       //sätter userId och steg
       when(mockedStepRepository.save(any(Step.class))).thenAnswer(new Answer<Step>() {
           @Override
           public Step answer(InvocationOnMock invocationOnMock) throws Throwable {
               Step step1 = (Step) invocationOnMock.getArguments()[0];
               step1.setUserId("userId");
               step1.setStepCount(stepDTO.getStepCount());
               final StepDTO stepDTO2 = new StepDTO(200,
                       LocalDateTime.parse("2020-01-01T00:00:01"),
                       LocalDateTime.parse("2020-01-01T01:00:02"),
                       LocalDateTime.parse("2020-01-01T02:00:03"));
               //100 +200
               step1.setStepCount(stepDTO.getStepCount()+stepDTO2.getStepCount());
               return step1;
           }

       });
       step=stepService.registerSteps(step.getUserId(), stepDTO).get();
        //användare finns i databas nu
        assertNotNull(step.getUserId());
        assertEquals("userId",step.getUserId() );
        //användare har steg
        assertNotNull(step.getStepCount());
        //användare har 300 steg (100+200)
        assertEquals(300, stepService.registerSteps(step.getUserId(), stepDTO).get().getStepCount());
    }

    @Test
    public void getLatestStepTest() {

        final String endDate = "2020-01-02T00:00:00";
        final String userID = "userId";
        final int expectedSteg = 100;

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).
                thenReturn(Optional.of(new Step("userTest3", 100,
                        LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                        LocalDateTime.parse("2020-01-02T00:00:00"))));

        LocalDateTime userEndTime = (stepService.getLatestStep(userID).get().getEnd());

        assertEquals(LocalDateTime.parse("2020-01-02T00:00:00"), userEndTime);
        assertEquals(stepService.getLatestStep(userID).get().getStepCount(), expectedSteg);
    }

    @Test
    public void shouldReturnUpdatedStepCount() {
        Step mockStep = new Step("userTest3", 100,
                LocalDateTime.parse("2020-01-02T01:00:00"), LocalDateTime.parse("2020-01-02T01:10:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        List<String> usersList = new ArrayList<>();
        usersList.add("userTest3");
        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);
        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).thenReturn(Optional.of(mockStep));
        when(mockedStepRepository.getAllUsers()).thenReturn(usersList);
        StepDTO stepDto = new StepDTO(50, LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T01:00:00"),
                LocalDateTime.parse("2020-01-02T02:00:00"));
        assertEquals(150,stepService.registerSteps("userTest3", stepDto).get().getStepCount());
    }

/*    //Detta testet är lite knäppt. ELler mycket. Det fyller noll funktion vid närmare eftertanke
    @Test
    public void shouldNotUpdateStepCountShouldCreateNewEntryInDB() {

        Step mockStepExistingInDB = new Step("userTest3", 6000,
                LocalDateTime.parse("2020-01-01T00:00:00"), LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T00:00:00"));

        Step mockStep = new Step("userTest3", 100,
                LocalDateTime.parse("2020-01-02T00:00:00"), LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T00:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(any(String.class))).thenReturn(Optional.of(mockStepExistingInDB));
        when(mockedStepRepository.save(any(Step.class))).thenReturn(mockStep);


        StepDTO stepDto = new StepDTO(100, LocalDateTime.parse("2020-01-01T00:00:00"),
                LocalDateTime.parse("2020-01-01T01:00:00"),
                LocalDateTime.parse("2020-01-01T02:00:00"));

        assertEquals(100,stepService.registerSteps("userTest3", stepDto).get().getStepCount());
    }*/

    @Test
    public void sortListByEndTime_test() {

        ArrayList<StepDTO> mockStepDTOList = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(10,
                LocalDateTime.parse("2020-08-21T00:01:10"),
                LocalDateTime.parse("2020-08-21T01:01:10"),
                LocalDateTime.parse("2020-08-21T02:01:20"));

        StepDTO stepDTO2 = new StepDTO(12,
                LocalDateTime.parse("2020-08-22T00:01:20"),
                LocalDateTime.parse("2020-08-22T01:01:20"),
                LocalDateTime.parse("2020-08-22T02:01:30"));

        StepDTO stepDTO3 = new StepDTO(13,
                LocalDateTime.parse("2020-08-22T00:01:10"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20"));

        mockStepDTOList.add(stepDTO1);
        mockStepDTOList.add(stepDTO2);
        mockStepDTOList.add(stepDTO3);

        assertEquals(stepService.sortListByEndTime(mockStepDTOList, false).get(0).getEndTime(), mockStepDTOList.get(0).getEndTime());
        assertEquals(stepService.sortListByEndTime(mockStepDTOList,false).get(1).getEndTime(), mockStepDTOList.get(2).getEndTime());
        assertEquals(stepService.sortListByEndTime(mockStepDTOList,false).get(2).getEndTime(), mockStepDTOList.get(1).getEndTime());
        assertEquals(stepService.sortListByEndTime(mockStepDTOList,true).get(0).getEndTime(), mockStepDTOList.get(1).getEndTime());

    }


    @Test
    public void registerMultipleSteps_test() {
        List<StepDTO> mockStepDTOList = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(10,
                LocalDateTime.parse("2020-08-21T00:01:10"),
                LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-08-21T02:01:20"));

        StepDTO stepDTO2 = new StepDTO(12,
                LocalDateTime.parse("2020-08-22T00:01:20"),
                LocalDateTime.parse("2020-08-22T01:01:20"),
                LocalDateTime.parse("2020-08-22T02:01:30"));

        StepDTO stepDTO3 = new StepDTO(13,
                LocalDateTime.parse("2020-08-22T00:01:10"),
                LocalDateTime.parse("2020-08-22T01:01:10"),
                LocalDateTime.parse("2020-08-22T02:01:20"));

        mockStepDTOList.add(stepDTO1);
        mockStepDTOList.add(stepDTO2);
        mockStepDTOList.add(stepDTO3);

        Step mockStep = new Step("idTest", 100,
                LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T00:00:00"),
                LocalDateTime.parse("2020-01-02T00:00:00"));

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString())).thenReturn(Optional.of(mockStep));
        when(mockedStepRepository.save(any())).thenReturn(mockStep);

        var test = stepService.registerMultipleSteps("idTest", mockStepDTOList);

        assertEquals(2, test.size());
        assertEquals(100, test.get(0).getStepCount());


    }

    @Test
    public void mergeStepDtoObjectsWithSameDate_withOneEndDateThatAlreadyExistsInDB_test() {

        List<StepDTO> mockStepDTOList1 = new ArrayList<>();
        List <StepDTO> mockStepDTOList2 = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(1,
                LocalDateTime.parse("2020-08-21T03:10:00"),
                LocalDateTime.parse("2020-08-21T03:20:00"),
                LocalDateTime.parse("2020-08-21T03:25:00"));

        StepDTO stepDTO2 = new StepDTO(2,
                LocalDateTime.parse("2020-08-21T05:20:00"),
                LocalDateTime.parse("2020-08-21T05:30:00"),
                LocalDateTime.parse("2020-08-21T05:35:00"));

        StepDTO stepDTO3 = new StepDTO(300,
                LocalDateTime.parse("2020-08-22T10:10:00"),
                LocalDateTime.parse("2020-08-22T10:20:00"),
                LocalDateTime.parse("2020-08-22T10:25:00"));

        StepDTO stepDTO4 = new StepDTO(400,
                LocalDateTime.parse("2020-08-22T11:40:00"),
                LocalDateTime.parse("2020-08-22T11:50:00"),
                LocalDateTime.parse("2020-08-22T11:55:00"));

        mockStepDTOList1.add(stepDTO2);
        mockStepDTOList1.add(stepDTO1);
        mockStepDTOList2.add(stepDTO3);
        mockStepDTOList2.add(stepDTO4);


        Map<Integer, List<StepDTO>> map = new HashMap<>();
        map.put(1000, mockStepDTOList1);
        map.put(2000, mockStepDTOList2);

        var modifiedList = stepService.mergeStepDtoObjectsWithSameDate(map);

        assertEquals(2,modifiedList.size());
        assertEquals(700,modifiedList.get(0).getStepCount());
        assertEquals(3,modifiedList.get(1).getStepCount());
        assertEquals(LocalDateTime.parse("2020-08-22T10:10:00"), modifiedList.get(0).getStartTime());
        assertEquals(LocalDateTime.parse("2020-08-21T03:10:00"), modifiedList.get(1).getStartTime());
        assertEquals(LocalDateTime.parse("2020-08-22T11:50:00"), modifiedList.get(0).getEndTime());
        assertEquals(LocalDateTime.parse("2020-08-21T05:30:00"), modifiedList.get(1).getEndTime());
    }

    @Test
    public void addOrUpdateStepDtoObjectsToDB_shouldReturnCompleteListOfStepObjectsForNewUser_test() {
        List <StepDTO> mockStepDTOList2 = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(1,
                LocalDateTime.parse("2020-08-21T03:10:00"),
                LocalDateTime.parse("2020-08-21T03:20:00"),
                LocalDateTime.parse("2020-08-21T03:25:00"));

        StepDTO stepDTO2 = new StepDTO(2,
                LocalDateTime.parse("2020-08-22T05:20:00"),
                LocalDateTime.parse("2020-08-22T05:30:00"),
                LocalDateTime.parse("2020-08-22T05:35:00"));

        StepDTO stepDTO3 = new StepDTO(300,
                LocalDateTime.parse("2020-08-23T10:10:00"),
                LocalDateTime.parse("2020-08-23T10:20:00"),
                LocalDateTime.parse("2020-08-23T10:25:00"));

        StepDTO stepDTO4 = new StepDTO(400,
                LocalDateTime.parse("2020-08-24T11:40:00"),
                LocalDateTime.parse("2020-08-24T11:50:00"),
                LocalDateTime.parse("2020-08-24T11:55:00"));

        mockStepDTOList2.add(stepDTO2);
        mockStepDTOList2.add(stepDTO1);
        mockStepDTOList2.add(stepDTO3);
        mockStepDTOList2.add(stepDTO4);

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString())).thenReturn(Optional.empty());

        assertEquals(4, stepService.addOrUpdateStepDtoObjectsToDB("test", mockStepDTOList2).size());

    }

    @Test
    public void addOrUpdateStepDtoObjectsToDB_withEarliestEndDateMatchingEntryInDB_test() {
        List <StepDTO> mockStepDTOList2 = new ArrayList<>();
        StepDTO stepDTO1 = new StepDTO(1,
                LocalDateTime.parse("2020-08-21T03:10:00"),
                LocalDateTime.parse("2020-08-21T03:20:00"),
                LocalDateTime.parse("2020-08-21T03:25:00"));

        StepDTO stepDTO2 = new StepDTO(2,
                LocalDateTime.parse("2020-08-22T05:20:00"),
                LocalDateTime.parse("2020-08-22T05:30:00"),
                LocalDateTime.parse("2020-08-22T05:35:00"));

        StepDTO stepDTO3 = new StepDTO(300,
                LocalDateTime.parse("2020-08-23T10:10:00"),
                LocalDateTime.parse("2020-08-23T10:20:00"),
                LocalDateTime.parse("2020-08-23T10:25:00"));

        StepDTO stepDTO4 = new StepDTO(400,
                LocalDateTime.parse("2020-08-24T11:40:00"),
                LocalDateTime.parse("2020-08-24T11:50:00"),
                LocalDateTime.parse("2020-08-24T11:55:00"));

        mockStepDTOList2.add(stepDTO2);
        mockStepDTOList2.add(stepDTO1);
        mockStepDTOList2.add(stepDTO3);
        mockStepDTOList2.add(stepDTO4);

        Step step = new Step("test",30,
                LocalDateTime.parse("2020-08-21T02:10:00"),
                LocalDateTime.parse("2020-08-21T02:20:00"),
                LocalDateTime.parse("2020-08-21T02:25:00") );

        when(mockedStepRepository.findFirstByUserIdOrderByEndTimeDesc(Mockito.anyString())).thenReturn(Optional.of(step));
        when(mockedStepRepository.save(any())).thenReturn(step);

        var test = stepService.addOrUpdateStepDtoObjectsToDB("test", mockStepDTOList2);

        assertEquals(31, test.get(0).getStepCount());
        assertEquals(4,test.size());

    }
}

