package it.epicode.pt_webApp.auth;

import it.epicode.pt_webApp.cliente.ClienteService;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    private final PersonalTrainerService personalTrainerService;

    private final ClienteService clienteService;

//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
//        appUserService.registerUser(
//                registerRequest.getUsername(),
//                registerRequest.getPassword(),
//                Set.of(Role.ROLE_USER) // Assegna il ruolo di default
//        );
//        return ResponseEntity.ok("Registrazione avvenuta con successo");
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        // Controlla se i ruoli sono stati forniti
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body("Il ruolo è obbligatorio (es. ROLE_USER o ROLE_PERSONAL_TRAINER)");
        }

        // Verifica il ruolo e registra l'utente
        if (registerRequest.getRoles().contains(Role.ROLE_USER)) {
            clienteService.registerCliente(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getNome(),
                    registerRequest.getCognome(),
                    registerRequest.getDataDiNascita()
            );
            return ResponseEntity.ok("Cliente registrato con successo");
        } else if (registerRequest.getRoles().contains(Role.ROLE_PERSONAL_TRAINER)) {
            personalTrainerService.registerPersonalTrainer(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getNome(),
                    registerRequest.getCognome(),
                    registerRequest.getDataDiNascita()
            );
            return ResponseEntity.ok("Personal Trainer registrato con successo");
        } else {
            return ResponseEntity.badRequest().body("Ruolo non valido. Usa ROLE_USER o ROLE_PERSONAL_TRAINER");
        }
    }


}
