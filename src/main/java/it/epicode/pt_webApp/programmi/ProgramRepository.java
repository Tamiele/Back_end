package it.epicode.pt_webApp.programmi;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findByPersonalTrainerId(Long personalTrainerId);

    List<Program> findByAssignedClientsId(Long clientId);


    @EntityGraph(attributePaths = {
            "personalTrainer",
            "personalTrainer.clienti",
            "weeks",
            "weeks.workouts",
            "weeks.workouts.workoutExercises",
            "weeks.workouts.workoutExercises.exercise"
    })
    Optional<Program> findById(@Param("id") Long id);




}
