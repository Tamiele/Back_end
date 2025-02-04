package it.epicode.pt_webApp.week;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekRepository extends JpaRepository< Week, Long> {

    List<Week> findByProgramId(Long programId);


}
