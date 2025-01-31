package it.epicode.pt_webApp.cliente;

import it.epicode.pt_webApp.auth.Role;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    //metodo che restiutisce tutti i clienti di un personal trainer
    public Page<Cliente> getAssignedClientsByTrainer(String username, Pageable pageable) {

        Page<Cliente> clienti = clienteRepository.findAllByPersonalTrainerUsername(username, pageable);

        return clienti;


    }

}
