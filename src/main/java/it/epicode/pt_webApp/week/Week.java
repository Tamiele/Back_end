package it.epicode.pt_webApp.week;

import it.epicode.pt_webApp.programmi.Program;
import it.epicode.pt_webApp.workout.Workout;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "week")
public class Week {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int weekNumber;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(mappedBy = "week", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Workout> workouts = new ArrayList<>();



}