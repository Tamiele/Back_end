package it.epicode.pt_webApp.workout_exercise;


import it.epicode.pt_webApp.esercizi.Exercise;
import it.epicode.pt_webApp.esercizi.ExerciseRepository;
import it.epicode.pt_webApp.workout.Workout;
import it.epicode.pt_webApp.workout.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkoutExerciseService {


    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public WorkoutExercise addExerciseToWorkout(Long workoutId, WorkoutExercise workoutExercise, Long exerciseId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout non trovato"));
        workoutExercise.setWorkout(workout);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise non trovato"));
        workoutExercise.setExercise(exercise);

        return workoutExerciseRepository.save(workoutExercise);
    }

    public WorkoutExerciseDTO convertToDTO(WorkoutExercise we) {
        WorkoutExerciseDTO dto = new WorkoutExerciseDTO();
        dto.setId(we.getId());
        dto.setSets(we.getSets());
        dto.setReps(we.getReps());
        dto.setRestType(we.getRestType());
        dto.setRestValue(we.getRestValue());
        dto.setWeight(we.getWeight());
        // Mappa anche l'Exercise se necessario:
        // dto.setExercise(exerciseMapper.convertToDTO(we.getExercise()));
        return dto;
    }
}
