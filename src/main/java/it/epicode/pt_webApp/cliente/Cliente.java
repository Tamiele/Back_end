package it.epicode.pt_webApp.cliente;


import it.epicode.pt_webApp.auth.AppUser;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

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
}
