package it.epicode.pt_webApp.programmi;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/programmi")
public class ProgramController {

    @Autowired
    private  ProgramService programService;

    @Autowired
    private  PersonalTrainerRepository personalTrainerRepository;

    @Autowired
    private ProgramRepository programRepository;


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





    // Recupera un Program tramite il suo id
    @GetMapping("/{id}")
    public ResponseEntity<Program> getProgramById(@PathVariable Long id) {
        Optional<Program> programOptional = programService.getProgramById(id);
        return programOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Recupera tutti i Program associati ad un determinato PersonalTrainer
    // L'endpoint è stato aggiornato per utilizzare il trainerId
    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Program>> getProgramsByTrainer(@PathVariable Long trainerId) {
        List<Program> programs = programService.getProgramsByPersonalTrainer(trainerId);
        return ResponseEntity.ok(programs);
    }

    // Aggiorna un Program esistente
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PutMapping("/{id}")
    public ResponseEntity<Program> updateProgram(@PathVariable Long id, @RequestBody Program program) {
        try {
            Program updatedProgram = programService.updateProgram(id, program);
            return ResponseEntity.ok(updatedProgram);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Elimina un Program
    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @GetMapping("/{programId}/structure")
    public ResponseEntity<ProgramResponseDTO> getProgramStructure(@PathVariable Long programId) {
        ProgramResponseDTO structureDTO = programService.getProgramStructure(programId);
        return ResponseEntity.ok(structureDTO);
    }


    @PreAuthorize("hasRole('ROLE_PERSONAL_TRAINER')")
    @PostMapping("/{programId}/assign/{clientId}")
    public ResponseEntity<ProgramResponseDTO> assignProgramToClient(
            @PathVariable Long programId,
            @PathVariable Long clientId) {
        Program program = programService.assignProgramToClient(programId, clientId);
        ProgramResponseDTO dto = programService.getProgramStructure(program.getId());
        return ResponseEntity.ok(dto);
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProgramResponseDTO>> getProgramsByClient(@PathVariable Long clientId) {

        List<Program> programs = programRepository.findByAssignedClientsId(clientId);
        List<ProgramResponseDTO> dtoList = programs.stream()
                .map(program -> ProgramMapper.toDto(program, clientId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }




}
