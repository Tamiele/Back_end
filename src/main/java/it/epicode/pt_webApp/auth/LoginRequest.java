package it.epicode.pt_webApp.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
