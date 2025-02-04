package it.epicode.pt_webApp.esercizi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;


    // Crea un nuovo Exercise
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping
    public ResponseEntity<Exercise> createExercise(@RequestBody Exercise exercise) {
        Exercise createdExercise = exerciseService.createExercise(exercise);
        return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
    }

    // Recupera un Exercise per ID
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable Long id) {
        Optional<Exercise> exerciseOpt = exerciseService.getExerciseById(id);
        return exerciseOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Recupera tutti gli Exercise
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    // Aggiorna un Exercise esistente
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        try {
            Exercise updatedExercise = exerciseService.updateExercise(id, exercise);
            return ResponseEntity.ok(updatedExercise);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cancella un Exercise
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
