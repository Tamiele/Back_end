package it.epicode.pt_webApp.week;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programmi/{programId}/weeks")
public class WeekController {

    @Autowired
    private  WeekService weekService;



    @PostMapping
    public ResponseEntity<WeekDTO> addWeek(@PathVariable Long programId, @RequestBody WeekDTO weekDTO) {
        Week week = new Week();
        week.setWeekNumber(weekDTO.getWeekNumber());
        // All'interno del service si assocerà il Program corrispondente (verifica che il programId esista)
        Week savedWeek = weekService.addWeekToProgram(programId, week);
        WeekDTO responseDTO = weekService.convertToDTO(savedWeek);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // Puoi aggiungere altri endpoint (GET, PUT, DELETE) per le week
}

