package it.epicode.pt_webApp.personal_trainer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalTrainerRepository extends JpaRepository<PersonalTrainer, Long> {


    boolean existsByUsername(String username);

    Optional<PersonalTrainer> findByUsername(String trainerUsername);
}
