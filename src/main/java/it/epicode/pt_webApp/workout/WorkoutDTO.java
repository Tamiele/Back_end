package it.epicode.pt_webApp.workout;

import it.epicode.pt_webApp.workout_exercise.WorkoutExerciseDTO;
import lombok.Data;
import java.util.List;

@Data
public class WorkoutDTO {
    private Long id;
    private String name;
    private boolean completed;
    private int dayOfWeek;
    private List<WorkoutExerciseDTO> exercises;
}
