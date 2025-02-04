package it.epicode.pt_webApp.workout_exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workouts/{workoutId}/exercises")
public class WorkoutExerciseController {

    @Autowired
    private  WorkoutExerciseService workoutExerciseService;



    @PostMapping
    public ResponseEntity<WorkoutExerciseDTO> addExerciseToWorkout(
            @PathVariable Long workoutId,
            @RequestBody WorkoutExerciseDTO workoutExerciseDTO) {
        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setSets(workoutExerciseDTO.getSets());
        workoutExercise.setReps(workoutExerciseDTO.getReps());
        workoutExercise.setRestType(workoutExerciseDTO.getRestType());
        workoutExercise.setRestValue(workoutExerciseDTO.getRestValue());
        workoutExercise.setWeight(workoutExerciseDTO.getWeight());
        // Dovrai recuperare l'entità Exercise in base all'id incluso nel workoutExerciseDTO.getExercise().getId()
        // e poi assegnarla a workoutExercise.setExercise(exercise);

        WorkoutExercise savedWE = workoutExerciseService.addExerciseToWorkout(workoutId, workoutExercise, workoutExerciseDTO.getExercise().getId());
        WorkoutExerciseDTO responseDTO = workoutExerciseService.convertToDTO(savedWE);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}

