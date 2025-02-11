package it.epicode.pt_webApp.personal_trainer;


import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.cliente.ClienteDTO;
import it.epicode.pt_webApp.cliente.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
public class PersonalTrainerController {


    private final ClienteService clienteService;

    private final PersonalTrainerService personalTrainerService;

    private final PersonalTrainerRepository personalTrainerRepository;


    @GetMapping
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<PersonalTrainer> getLoggedTrainerProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato"));

        return ResponseEntity.ok(trainer);
    }


    @PutMapping
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<PersonalTrainer> updateLoggedTrainer(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PersonalTrainer personalTrainer) {

        String username = userDetails.getUsername();

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal Trainer non trovato"));


        if (personalTrainer.getUsername() != null && !trainer.getUsername().equals(personalTrainer.getUsername()) &&
                personalTrainerRepository.existsByUsername(personalTrainer.getUsername())) {
            throw new RuntimeException("Username già in uso, scegline un altro");
        }

        if (personalTrainer.getEmail() != null && !trainer.getEmail().equals(personalTrainer.getEmail()) &&
                personalTrainerRepository.existsByEmail(personalTrainer.getEmail())) {
            throw new RuntimeException("Email già in uso, scegline un'altra");
        }

        if (personalTrainer.getUsername() != null) trainer.setUsername(personalTrainer.getUsername());
        if (personalTrainer.getEmail() != null) trainer.setEmail(personalTrainer.getEmail());
        if (personalTrainer.getNome() != null) trainer.setNome(personalTrainer.getNome());
        if (personalTrainer.getCognome() != null) trainer.setCognome(personalTrainer.getCognome());
        if (personalTrainer.getDataDiNascita() != null) trainer.setDataDiNascita(personalTrainer.getDataDiNascita());

        return ResponseEntity.ok(personalTrainerRepository.save(trainer));
    }


    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Map<String, String>> deleteLoggedTrainer(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal Trainer non trovato"));

        personalTrainerService.deletePersonalTrainer(trainer.getId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Il tuo account da Personal Trainer è stato eliminato con successo.");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/assign-client/{clientId}")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<String> assignClientToLoggedTrainer(
            @PathVariable Long clientId,
            Principal principal
    ) {
        String trainerUsername = principal.getName();
        personalTrainerService.assignClienteToTrainerByUsername(trainerUsername, clientId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/remove-client/{clientId}")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Map<String, String>> removeClient(
            @PathVariable Long clientId,
            Principal principal
    ) {
        String trainerUsername = principal.getName();
        Map<String, String> response = new HashMap<>();

        try {
            personalTrainerService.removeClientFromTrainer(trainerUsername, clientId);
            response.put("message", "Cliente rimosso con successo dalla lista dei propri clienti");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
