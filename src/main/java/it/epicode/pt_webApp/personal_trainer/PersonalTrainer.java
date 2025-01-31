package it.epicode.pt_webApp.personal_trainer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.pt_webApp.auth.AppUser;
import it.epicode.pt_webApp.cliente.Cliente;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "personal_trainers")
public class PersonalTrainer extends AppUser {

    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;

    @OneToMany(mappedBy = "personalTrainer", cascade = CascadeType.PERSIST, orphanRemoval = false)
    @JsonIgnore
    private List<Cliente> clienti = new ArrayList<>();
}

