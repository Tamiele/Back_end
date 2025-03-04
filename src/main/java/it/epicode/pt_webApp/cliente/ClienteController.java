package it.epicode.pt_webApp.cliente;


import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/clienti")

public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;


    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Cliente> getLoggedClientProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato"));

        return ResponseEntity.ok(cliente);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Cliente> updateLoggedClient(@AuthenticationPrincipal UserDetails userDetails,
                                                      @RequestBody ClienteDTO clienteDTO) {

        String username = userDetails.getUsername();


        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato"));


        Cliente updatedCliente = clienteService.updateCliente(cliente.getId(), clienteDTO);

        return ResponseEntity.ok(updatedCliente);
    }


    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteLoggedClient(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();


        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato"));


        clienteService.deleteCliente(cliente.getId());

        return ResponseEntity.ok("Il tuo account è stato eliminato con successo.");
    }


    @GetMapping("/myClient")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Page<Cliente>> getMyClients(

            @AuthenticationPrincipal UserDetails user, Pageable pageable

    ) {
        Page<Cliente> clients = clienteService.getAssignedClientsByTrainer(user.getUsername(), pageable);
        return ResponseEntity.ok(clients);
    }


    //ricerca clienti per username e email
    @GetMapping("/search-clients-byTrainer")
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<?> searchClient(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        if (username != null) {
            Optional<ClienteDTO> client = clienteService.searchClientByUsername(username);
            return client.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body("Il Cliente" + "" + username + "" + "è gia assegnato ad un altro Personal Trainer"));
        } else if (email != null) {
            Optional<ClienteDTO> client = clienteService.searchClientByEmail(email);
            return client.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body("Il Cliente con e-mail:" + "" + email + "" + " è gia assegnato ad un altro Personal Trainer"));
        } else {
            return ResponseEntity.badRequest().body("Devi fornire almeno un parametro: username o email");
        }
    }

}
