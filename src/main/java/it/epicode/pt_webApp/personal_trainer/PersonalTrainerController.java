package it.epicode.pt_webApp.personal_trainer;


import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.cliente.ClienteDTO;
import it.epicode.pt_webApp.cliente.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/personalTrainer")
@RequiredArgsConstructor
public class PersonalTrainerController {


    private final ClienteService clienteService;

    private final PersonalTrainerService personalTrainerService;


    //ricerca clienti per username e email
    @GetMapping("/search-client")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Object> searchClient(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        if (username != null) {
            Optional<ClienteDTO> client = clienteService.searchClientByUsername(username);
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(404).body("Cliente non trovato con username: " + username);
            }
        } else if (email != null) {
            Optional<ClienteDTO> client = clienteService.searchClientByEmail(email);
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(404).body("Cliente non trovato con email: " + email);
            }
        } else {
            return ResponseEntity.badRequest().body("Devi fornire almeno un parametro: username o email");
        }
    }


    @PostMapping("/assign-client/{clientId}")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<String> assignClientToLoggedTrainer(
            @PathVariable Long clientId,
            Principal principal
    ) {
        String trainerUsername = principal.getName(); // Ottieni l'username del trainer loggato
        personalTrainerService.assignClienteToTrainerByUsername(trainerUsername, clientId);
        return ResponseEntity.ok("Cliente assegnato al Personal Trainer con successo");
    }


    @GetMapping("/my-clients")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Page<ClienteDTO>> getMyClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal // Ottieni il personal trainer loggato
    ) {
        String trainerUsername = principal.getName();
        Pageable pageable = PageRequest.of(page, size); // Configura la paginazione

        Page<ClienteDTO> clients = personalTrainerService.getAssignedClientsByTrainer(trainerUsername, pageable);
        return ResponseEntity.ok(clients);
    }


    @DeleteMapping("/remove-client/{clientId}")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<String> removeClient(
            @PathVariable Long clientId,
            Principal principal // Ottieni il personal trainer loggato
    ) {
        String trainerUsername = principal.getName();

        try {
            personalTrainerService.removeClientFromTrainer(trainerUsername, clientId);
            return ResponseEntity.ok("Cliente rimosso con successo dalla lista dei propri clienti");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }





}
