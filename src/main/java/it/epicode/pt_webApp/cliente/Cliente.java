package it.epicode.pt_webApp.cliente;


import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.pt_webApp.auth.AppUser;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import it.epicode.pt_webApp.programmi.Program;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "clients")
public class Cliente extends AppUser {

    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;

    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private PersonalTrainer personalTrainer;

    @ManyToMany(mappedBy = "assignedClients")
    @JsonIgnore
    private List<Program> assignedPrograms;




}
