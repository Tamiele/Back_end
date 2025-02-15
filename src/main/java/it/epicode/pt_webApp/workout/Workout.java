package it.epicode.pt_webApp.workout;

import it.epicode.pt_webApp.week.Week;
import it.epicode.pt_webApp.workout_exercise.WorkoutExercise;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private boolean completed;
    private int dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "week_id")
    private Week week;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExercise> workoutExercises;

}