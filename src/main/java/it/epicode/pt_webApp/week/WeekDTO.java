package it.epicode.pt_webApp.week;

import it.epicode.pt_webApp.workout.WorkoutDTO;
import lombok.Data;
import java.util.List;

@Data
public class WeekDTO {
    private Long id;
    private int weekNumber;
    private List<WorkoutDTO> workouts;
}
