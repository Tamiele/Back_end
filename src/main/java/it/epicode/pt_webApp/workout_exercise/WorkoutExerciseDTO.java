package it.epicode.pt_webApp.workout_exercise;

import it.epicode.pt_webApp.Enum.RestType;
import it.epicode.pt_webApp.esercizi.ExerciseDTO;

import lombok.Data;

@Data
public class WorkoutExerciseDTO {
    private Long id;
    private int sets;
    private int reps;
    private RestType restType;
    private int restValue;
    private double weight;
    private ExerciseDTO exercise;
}
