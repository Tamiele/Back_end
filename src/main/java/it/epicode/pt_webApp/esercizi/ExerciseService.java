package it.epicode.pt_webApp.esercizi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExerciseService {

    @Autowired
    private  ExerciseRepository exerciseRepository;


    // Crea un nuovo Exercise
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    // Aggiorna un Exercise esistente
    public Exercise updateExercise(Long id, Exercise exercise) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        existingExercise.setName(exercise.getName());
        existingExercise.setNameEn(exercise.getNameEn());
        existingExercise.setMuscleGroup(exercise.getMuscleGroup());
        existingExercise.setDescription(exercise.getDescription());
        existingExercise.setDescriptionEn(exercise.getDescriptionEn());

        return exerciseRepository.save(existingExercise);
    }

    // Cancella un Exercise
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new RuntimeException("Exercise not found");
        }
        exerciseRepository.deleteById(id);
    }

    // Recupera un Exercise per ID
    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    // Recupera tutti gli Exercise
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }


}
