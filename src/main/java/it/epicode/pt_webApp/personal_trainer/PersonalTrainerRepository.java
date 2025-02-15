package it.epicode.pt_webApp.personal_trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalTrainerRepository extends JpaRepository<PersonalTrainer, Long> {

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    Optional<PersonalTrainer> findByUsername(String trainerUsername);
}
