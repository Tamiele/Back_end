package it.epicode.pt_webApp.workout_exercise;

import it.epicode.pt_webApp.Enum.RestType;
import it.epicode.pt_webApp.esercizi.Exercise;
import it.epicode.pt_webApp.workout.Workout;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Entity
@Table(name = "workoutExercises")
public class WorkoutExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private int sets;

    @Min(1)
    private int reps;

    @Enumerated(EnumType.STRING)
    @Column(name = "rest_type")
    private RestType restType;


    private int restValue;
    private double weight;

    @ManyToOne
    @JoinColumn(name = "workout_id",nullable = false)
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
}
