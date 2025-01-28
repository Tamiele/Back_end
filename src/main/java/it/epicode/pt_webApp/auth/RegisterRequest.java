package it.epicode.pt_webApp.auth;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RegisterRequest {

    private String username;
    private String password;
    private String email;
    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;
    private Set<Role> roles;
}
