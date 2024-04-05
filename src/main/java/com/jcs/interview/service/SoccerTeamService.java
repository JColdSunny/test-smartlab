package com.jcs.interview.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcs.interview.dto.LeagueDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SoccerTeamService {
    private final GameScheduleService gameScheduleService;
    private final ObjectMapper objectMapper;


    public String prepareSchedule(MultipartFile file) {
        LeagueDto leagueDto = parseJsonFIle(file);

        return gameScheduleService.generate(leagueDto);
    }

    private LeagueDto parseJsonFIle(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getInputStream(), LeagueDto.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON file", e);
        }
    }

}
