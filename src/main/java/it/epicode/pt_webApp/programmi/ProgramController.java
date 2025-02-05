package it.epicode.pt_webApp.programmi;

import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.cliente.ClienteRepository;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerDTO;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerRepository;

import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import it.epicode.pt_webApp.week.WeekDTO;
import it.epicode.pt_webApp.workout.WorkoutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/programmi")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @Autowired
    private PersonalTrainerRepository personalTrainerRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ClienteRepository clienteRepository;


    // Crea un nuovo Programma
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping
    public ResponseEntity<ProgramResponseDTO> createProgram(
            @RequestBody ProgramRequestDTO requestDTO,
            Authentication auth
    ) {

        User userDetails = (User) auth.getPrincipal();

        String username = userDetails.getUsername();

        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Personal Trainer non trovato per username: " + username));


        Program program = new Program();
        program.setName(requestDTO.getName());
        program.setDescription(requestDTO.getDescription());
        program.setTemplate(requestDTO.isTemplate());
        program.setAssigned(false);
        program.setPersonalTrainer(trainer);

        Program savedProgram = programService.createProgram(program);

        ProgramResponseDTO responseDTO = ProgramMapper.toDto(savedProgram);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }




    // Aggiorna un Program esistente
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<Program> updateProgram(
            @PathVariable Long id,
            @RequestBody Program program,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        Program existingProgram = programService.getProgramById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programma non trovato"));


        if (!existingProgram.getPersonalTrainer().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non hai il permesso di modificare questo programma.");
        }

        Program updatedProgram = programService.updateProgram(id, program);
        return ResponseEntity.ok(updatedProgram);
    }


    // Elimina un Program
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();


        Program existingProgram = programService.getProgramById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programma non trovato"));


        if (!existingProgram.getPersonalTrainer().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non hai il permesso di eliminare questo programma.");
        }

        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @GetMapping("/my-programs")
    public ResponseEntity<List<ProgramResponseDTO>> getMyPrograms(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();


        PersonalTrainer trainer = personalTrainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal Trainer non trovato"));

        List<Program> programs = programRepository.findByPersonalTrainerId(trainer.getId());

        List<ProgramResponseDTO> dtoList = programs.stream()
                .map(program -> programService.getProgramStructure(program.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }



    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping("/{programId}/assign/{clientId}")
    public ResponseEntity<ProgramResponseDTO> assignProgramToClient(
            @PathVariable Long programId,
            @PathVariable Long clientId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        Program existingProgram = programService.getProgramById(programId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programma non trovato"));

        if (!existingProgram.getPersonalTrainer().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non puoi assegnare questo programma.");
        }

        Program program = programService.assignProgramToClient(programId, clientId);
        ProgramResponseDTO dto = programService.getProgramStructure(program.getId());
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/client")
    public ResponseEntity<List<ProgramResponseDTO>> getProgramsByLoggedClient(@AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();

        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente non trovato"));

        Long clientId = cliente.getId();

        List<Program> programs = programRepository.findByAssignedClientsId(clientId);
        List<ProgramResponseDTO> dtoList = programs.stream()
                .map(program -> ProgramMapper.toDto(program, clientId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }


}
