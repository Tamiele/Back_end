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

    //registra un personalTrainer questo metodo lo richiamo nell'AuthController
    public PersonalTrainer registerPersonalTrainer(String username, String password, String email, String nome, String cognome, LocalDate dataDiNascita) {
        if (personalTrainerRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        PersonalTrainer personalTrainer = new PersonalTrainer();
        personalTrainer.setUsername(username);
        personalTrainer.setPassword(passwordEncoder.encode(password));
        personalTrainer.setEmail(email);
        personalTrainer.setNome(nome);
        personalTrainer.setCognome(cognome);
        personalTrainer.setDataDiNascita(dataDiNascita);
        personalTrainer.setRoles(Set.of(Role.ROLE_PERSONAL_TRAINER));

        return personalTrainerRepository.save(personalTrainer);
    }

    //get dei dati del personal loggato
    public PersonalTrainerDTO getPersonalTrainerDetails(String username) {
        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato"));

        // Converti il PersonalTrainer in DTO
        PersonalTrainerDTO dto = new PersonalTrainerDTO();
        BeanUtils.copyProperties(trainer, dto);

        // Converti la lista dei clienti associati in DTO
        List<ClienteDTO> clientiDTO = trainer.getClienti().stream()
                .map(cliente -> {
                    ClienteDTO clienteDTO = new ClienteDTO();
                    BeanUtils.copyProperties(cliente, clienteDTO);
                    return clienteDTO;
                })
                .collect(Collectors.toList());

        dto.setClienti(clientiDTO);

        return dto;
    }

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

        // Assegna il cliente al trainer
        cliente.setPersonalTrainer(trainer);
        trainer.getClienti().add(cliente);


        personalTrainerRepository.save(trainer);
    }

    //metodo che restiutisce tutti i clienti di un personal trainer
    public Page<ClienteDTO> getAssignedClientsByTrainer(String trainerUsername, Pageable pageable) {

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Personal Trainer non trovato con username: " + trainerUsername));


        Page<Cliente> clientsPage = clienteRepository.findAllByPersonalTrainer(trainer, pageable);


        List<ClienteDTO> clientsDTO = clientsPage.getContent().stream().map(cliente -> {
            ClienteDTO dto = new ClienteDTO();
            dto.setId(cliente.getId());
            dto.setUsername(cliente.getUsername());
            dto.setEmail(cliente.getEmail());
            dto.setNome(cliente.getNome());
            dto.setCognome(cliente.getCognome());
            dto.setDataDiNascita(cliente.getDataDiNascita());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(clientsDTO, pageable, clientsPage.getTotalElements());
    }

    //rimuove il cliente dal personal
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
