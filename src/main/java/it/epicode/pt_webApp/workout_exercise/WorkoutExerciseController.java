package it.epicode.pt_webApp.workout_exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutExerciseController {

    @Autowired
    private  WorkoutExerciseService workoutExerciseService;


    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping("/{workoutId}/exercises")
    public ResponseEntity<WorkoutExerciseDTO> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @RequestBody WorkoutExerciseDTO workoutExerciseDTO) {
        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setSets(workoutExerciseDTO.getSets());
        workoutExercise.setReps(workoutExerciseDTO.getReps());
        workoutExercise.setRestType(workoutExerciseDTO.getRestType());
        workoutExercise.setRestValue(workoutExerciseDTO.getRestValue());
        workoutExercise.setWeight(workoutExerciseDTO.getWeight());


        WorkoutExercise savedWE = workoutExerciseService.addExerciseToWorkout(workoutId, workoutExercise, workoutExerciseDTO.getExercise().getId());
        WorkoutExerciseDTO responseDTO = workoutExerciseService.convertToDTO(savedWE);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}

