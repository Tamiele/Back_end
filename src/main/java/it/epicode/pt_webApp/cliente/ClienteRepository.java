package it.epicode.pt_webApp.cliente;

import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<Cliente> findByUsername(String username);

    Optional<Cliente> findByEmail(String email);

    Page<Cliente> findAllByPersonalTrainerUsername(String username, Pageable pageable);


    @Query("SELECT c FROM Cliente c WHERE c.email = :email AND c.personalTrainer IS NULL")
    Optional<Cliente> findAvailableByEmail(@Param("email") String email);

    @Query("SELECT c FROM Cliente c WHERE c.username = :username AND c.personalTrainer IS NULL")
    Optional<Cliente> findAvailableByUsername(@Param("username") String username);

}
