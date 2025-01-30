package it.epicode.pt_webApp.personal_trainer;

import it.epicode.pt_webApp.cliente.ClienteDTO;
import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PersonalTrainerDTO {
    private Long id;
    private String username;
    private String email;
    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;
    private List<ClienteDTO> clienti;
}
