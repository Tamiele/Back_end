package it.epicode.pt_webApp.cliente;

import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByUsername(String username);

    Optional<Cliente> findByUsername(String username);

    Optional<Cliente> findByEmail(String email);

    Page<Cliente> findAllByPersonalTrainerUsername(String username, Pageable pageable);
}
