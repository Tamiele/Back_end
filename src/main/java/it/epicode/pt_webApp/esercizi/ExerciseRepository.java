package it.epicode.pt_webApp.esercizi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByName(String name);


    List<Exercise> findByMuscleGroupIgnoreCase(String muscleGroup);
}
