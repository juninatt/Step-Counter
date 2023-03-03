package com.nexergroup.boostapp.java.step.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.nexergroup.boostapp.java.step.dto.starpointdto.BulkUserStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.RequestStarPointsDTO;
import com.nexergroup.boostapp.java.step.dto.starpointdto.StarPointDateDTO;
import com.nexergroup.boostapp.java.step.service.StarPointService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
public class StarPointControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private StarPointService starPointService;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StarPointController(starPointService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getStarPointsByUsers_WithValidInput_ReturnsStatusOKandCorrectContent() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        String url = "/starpoints/";
        List<String> users = new ArrayList<>(List.of("User1", "User2"));
        var startTime = LocalDateTime.parse("2020-08-21T00:01:00").atZone(ZoneId.systemDefault());
        var endTime = LocalDateTime.parse("2021-08-21T00:01:00").atZone(ZoneId.systemDefault());

        RequestStarPointsDTO requestStarPointsDTO = new RequestStarPointsDTO(users, startTime, endTime);

        List<BulkUserStarPointsDTO> bulkUserStarPointsDTOList = new ArrayList<>();
        bulkUserStarPointsDTOList.add(new BulkUserStarPointsDTO("User1", new StarPointDateDTO("Steps", "Walking", startTime.toString(), endTime.toString(), 20)));
        bulkUserStarPointsDTOList.add(new BulkUserStarPointsDTO("User2", new StarPointDateDTO("Steps", "Running", startTime.toString(), endTime.toString(), 40)));

        when(starPointService.getStarPointsByMultipleUsers(any(RequestStarPointsDTO.class))).thenReturn(bulkUserStarPointsDTOList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE).content(objectMapper.writeValueAsString(requestStarPointsDTO)).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        var result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andReturn();

        String expectedJsonResponse = objectMapper.writeValueAsString(bulkUserStarPointsDTOList);
        String actualJsonResponse = result.getResponse().getContentAsString();

        assertEquals(expectedJsonResponse, actualJsonResponse);
    }
}
