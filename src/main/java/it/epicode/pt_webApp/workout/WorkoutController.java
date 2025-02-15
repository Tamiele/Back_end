package it.epicode.pt_webApp.workout;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weeks")
public class WorkoutController {

    @Autowired
    private  WorkoutService workoutService;

    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping("/{weekId}/workouts")
    public ResponseEntity<WorkoutDTO> addWorkout(@PathVariable Long weekId, @RequestBody WorkoutDTO workoutDTO) {
        Workout workout = new Workout();
        workout.setName(workoutDTO.getName());
        workout.setCompleted(workoutDTO.isCompleted());
        workout.setDayOfWeek(workoutDTO.getDayOfWeek());

        Workout savedWorkout = workoutService.addWorkoutToWeek(weekId, workout);
        WorkoutDTO responseDTO = workoutService.convertToDTO(savedWorkout);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
