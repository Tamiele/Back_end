package it.epicode.pt_webApp.auth;

import it.epicode.pt_webApp.cliente.ClienteService;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    private final PersonalTrainerService personalTrainerService;

    private final ClienteService clienteService;



    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest) {
        // Controlla se i ruoli sono stati forniti
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Il ruolo è obbligatorio (es. ROLE_USER o ROLE_PERSONAL_TRAINER)");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Verifica il ruolo e registra l'utente
        Map<String, String> response = new HashMap<>();
        if (registerRequest.getRoles().contains(Role.ROLE_USER)) {
            clienteService.registerCliente(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getNome(),
                    registerRequest.getCognome(),
                    registerRequest.getDataDiNascita()
            );
            response.put("message", "Cliente registrato con successo");
            return ResponseEntity.ok(response);
        } else if (registerRequest.getRoles().contains(Role.ROLE_PERSONAL_TRAINER)) {
            personalTrainerService.registerPersonalTrainer(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getNome(),
                    registerRequest.getCognome(),
                    registerRequest.getDataDiNascita()
            );
            response.put("message", "Personal Trainer registrato con successo");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Ruolo non valido. Usa ROLE_USER o ROLE_PERSONAL_TRAINER");
            return ResponseEntity.badRequest().body(response);
        }
    }


}
