package it.epicode.pt_webApp.week;


import it.epicode.pt_webApp.programmi.Program;
import it.epicode.pt_webApp.programmi.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WeekService {

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private ProgramRepository programRepository;


    public Week addWeekToProgram(Long programId, Week week) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program non trovato"));

        if (week.getWorkouts() == null) {
            week.setWorkouts(new ArrayList<>());
        }

        week.setProgram(program);
        return weekRepository.save(week);
    }

    public WeekDTO convertToDTO(Week week) {
        WeekDTO dto = new WeekDTO();
        dto.setId(week.getId());
        dto.setWeekNumber(week.getWeekNumber());

        return dto;
    }
}

