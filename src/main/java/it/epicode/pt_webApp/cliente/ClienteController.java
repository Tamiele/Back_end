package it.epicode.pt_webApp.cliente;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clienti")

public class ClienteController {

    @Autowired
    private ClienteService clienteService;


    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateProfile(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        Cliente updateCliente = clienteService.updateCliente(id, clienteDTO);
        return ResponseEntity.ok(updateCliente);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePersonalTrainer(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.ok("Cliente eliminato con successo");
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Page<Cliente>> getMyClients(

            @AuthenticationPrincipal UserDetails user, Pageable pageable

    ) {

        Page<Cliente> clients = clienteService.getAssignedClientsByTrainer(user.getUsername(), pageable);
        return ResponseEntity.ok(clients);
    }


    //ricerca clienti per username e email
    @GetMapping("/search-clients")
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
}
