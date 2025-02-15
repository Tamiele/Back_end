package it.epicode.pt_webApp.personal_trainer;

import it.epicode.pt_webApp.auth.Role;
import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.cliente.ClienteDTO;
import it.epicode.pt_webApp.cliente.ClienteRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonalTrainerService {
    @Autowired
    private PersonalTrainerRepository personalTrainerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClienteRepository clienteRepository;




    //metodo per modificare il profilo personalTrainer
    public PersonalTrainer updateProfileTrainer(Long id, PersonalTrainer personalTrainer) {
        PersonalTrainer trainer = personalTrainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato"));


        if (!trainer.getUsername().equals(personalTrainer.getUsername()) &&
                personalTrainerRepository.existsByUsername(personalTrainer.getUsername())) {
            throw new RuntimeException("Username già in uso, scegline un altro");
        }

        if (!trainer.getEmail().equals(personalTrainer.getEmail()) &&
                personalTrainerRepository.existsByEmail(personalTrainer.getEmail())) {
            throw new RuntimeException("Email già in uso, scegline un'altra");
        }

        trainer.setUsername(personalTrainer.getUsername());
        trainer.setEmail(personalTrainer.getEmail());
        trainer.setNome(personalTrainer.getNome());
        trainer.setCognome(personalTrainer.getCognome());
        trainer.setDataDiNascita(personalTrainer.getDataDiNascita());

        return personalTrainerRepository.save(trainer);
    }

    //metodo per eliminare il personalTrainer dal db
    public void deletePersonalTrainer(Long id) {
        if (!personalTrainerRepository.existsById(id)) {
            throw new EntityNotFoundException("Personal Trainer non trovato");
        }
        personalTrainerRepository.deleteById(id);
    }


    //metodo per aggiungere un cliente ad un personal trainer
    @Transactional
    public void assignClienteToTrainerByUsername(String trainerUsername, Long clienteId) {

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato con username: " + trainerUsername));

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato con ID: " + clienteId));

        if (cliente.getPersonalTrainer() != null) {
            throw new IllegalStateException("Il cliente è già assegnato a un altro Personal Trainer");
        }

        // Assegna il cliente al trainer
        cliente.setPersonalTrainer(trainer);
        trainer.getClienti().add(cliente);

        // Salva entrambi per aggiornare il database
        clienteRepository.save(cliente); // 🔹 IMPORTANTE: salva il cliente
        personalTrainerRepository.save(trainer);
    }


    //personal rimuove il cliente dai suoi preferiti
    @Transactional
    public void removeClientFromTrainer(String trainerUsername, Long clientId) {

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato con username: " + trainerUsername));


        Cliente cliente = clienteRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato con ID: " + clientId));


        if (!trainer.getClienti().contains(cliente)) {
            throw new IllegalStateException("Il cliente non è associato a questo Personal Trainer");
        }


        trainer.getClienti().remove(cliente);


        cliente.setPersonalTrainer(null);


        clienteRepository.save(cliente);
    }


}
