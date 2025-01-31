package it.epicode.pt_webApp.cliente;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clienti")

public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    public ResponseEntity<Page<Cliente>> getMyClients(

            @AuthenticationPrincipal UserDetails user, Pageable pageable

    ) {

        Page<Cliente> clients = clienteService.getAssignedClientsByTrainer(user.getUsername(), pageable);
        return ResponseEntity.ok(clients);
    }
}
