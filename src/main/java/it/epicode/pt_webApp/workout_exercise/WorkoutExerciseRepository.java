package it.epicode.pt_webApp.workout_exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    List<WorkoutExercise> findByWorkoutId(Long workoutId);

    void deleteByWorkoutId(Long id);
}
