package it.epicode.pt_webApp.cliente;

import it.epicode.pt_webApp.auth.Role;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //registra cliente
    public Cliente registerCliente(String username, String password, String email, String nome, String cognome, LocalDate dataDiNascita) {
        if (clienteRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        Cliente cliente = new Cliente();
        cliente.setUsername(username);
        cliente.setPassword(passwordEncoder.encode(password));
        cliente.setEmail(email);
        cliente.setNome(nome);
        cliente.setCognome(cognome);
        cliente.setDataDiNascita(dataDiNascita);
        cliente.setRoles(Set.of(Role.ROLE_USER));

        return clienteRepository.save(cliente);
    }


    public Optional<ClienteDTO> searchClientByUsername(String username) {
        return clienteRepository.findByUsername(username)
                .map(cliente -> {
                    ClienteDTO dto = new ClienteDTO();
                    dto.setId(cliente.getId());
                    dto.setUsername(cliente.getUsername());
                    dto.setEmail(cliente.getEmail());
                    dto.setNome(cliente.getNome());
                    dto.setCognome(cliente.getCognome());
                    dto.setDataDiNascita(cliente.getDataDiNascita());
                    return dto;
                });
    }

    public Optional<ClienteDTO> searchClientByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .map(cliente -> {
                    ClienteDTO dto = new ClienteDTO();
                    dto.setId(cliente.getId());
                    dto.setUsername(cliente.getUsername());
                    dto.setEmail(cliente.getEmail());
                    dto.setNome(cliente.getNome());
                    dto.setCognome(cliente.getCognome());
                    dto.setDataDiNascita(cliente.getDataDiNascita());
                    return dto;
                });
    }

}
