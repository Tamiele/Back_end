package it.epicode.pt_webApp.cliente;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteDTO {
    private Long id;
    private String username;
    private String email;
    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;
    private int personalTrainerId;
}
