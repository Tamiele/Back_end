package it.epicode.pt_webApp.week;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programmi")
public class WeekController {

    @Autowired
    private  WeekService weekService;


    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping("/{programId}/weeks")

    public ResponseEntity<WeekDTO> addWeek(@PathVariable Long programId, @RequestBody WeekDTO weekDTO) {
        Week week = new Week();
        week.setWeekNumber(weekDTO.getWeekNumber());

        Week savedWeek = weekService.addWeekToProgram(programId, week);
        WeekDTO responseDTO = weekService.convertToDTO(savedWeek);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


}

