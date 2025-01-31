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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PersonalTrainer updateProfile(Long id, PersonalTrainerDTO updateRequest) {
        PersonalTrainer trainer = personalTrainerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal Trainer non trovato"));


        if (!trainer.getUsername().equals(updateRequest.getUsername()) &&
                personalTrainerRepository.existsByUsername(updateRequest.getUsername())) {
            throw new RuntimeException("Username già in uso, scegline un altro");
        }

        if (!trainer.getEmail().equals(updateRequest.getEmail()) &&
                personalTrainerRepository.existsByEmail(updateRequest.getEmail())) {
            throw new RuntimeException("Email già in uso, scegline un'altra");
        }

        trainer.setUsername(updateRequest.getUsername());
        trainer.setEmail(updateRequest.getEmail());
        trainer.setNome(updateRequest.getNome());
        trainer.setCognome(updateRequest.getCognome());
        trainer.setDataDiNascita(updateRequest.getDataDiNascita());

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
    public void assignClienteToTrainerByUsername(String trainerUsername, Long clienteId) {

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato con username: " + trainerUsername));


        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato con ID: " + clienteId));


        if (cliente.getPersonalTrainer() != null) {
            throw new IllegalStateException("Il cliente è già assegnato a un altro Personal Trainer");
        }


        cliente.setPersonalTrainer(trainer);
        trainer.getClienti().add(cliente);


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
