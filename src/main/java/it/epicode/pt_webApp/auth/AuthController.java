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
        AuthResponse authResponse = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Il ruolo è obbligatorio (es. ROLE_USER o ROLE_PERSONAL_TRAINER)");
            return ResponseEntity.badRequest().build();
        }

        AppUser newUser = appUserService.register(registerRequest);
        return ResponseEntity.ok(newUser);
    }



}
