package com.jcs.interview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcs.interview.dto.LeagueDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

@SpringJUnitConfig({SoccerTeamService.class})
class SoccerTeamServiceTest {

    @MockBean
    GameScheduleService mockGameScheduleService;

    @MockBean
    ObjectMapper mockObjectMapper;

    @Autowired
    SoccerTeamService soccerTeamService;

    @Test
    void testPrepareScheduleWhenCorrectJson() throws Exception {
        LeagueDto mockLeagueDto = new LeagueDto("", "", List.of());
        MultipartFile multipartFile = new MockMultipartFile(
            "data", "filename.txt", "text/plain", "requestData".getBytes());
        when(mockObjectMapper.readValue(any(InputStream.class), eq(LeagueDto.class)))
            .thenReturn(mockLeagueDto);
        String expectedSchedule = "game schedule";
        when(mockGameScheduleService.generate(any(LeagueDto.class))).thenReturn(expectedSchedule);

        String actualSchedule = soccerTeamService.prepareSchedule(multipartFile);

        assertEquals(expectedSchedule, actualSchedule);
    }

    @Test
    void testPrepareScheduleWhenIncorrectJson() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile(
            "data", "filename.txt", "text/plain", "requestData".getBytes());
        when(mockObjectMapper.readValue(any(InputStream.class), eq(LeagueDto.class)))
            .thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> soccerTeamService.prepareSchedule(multipartFile));
    }

}