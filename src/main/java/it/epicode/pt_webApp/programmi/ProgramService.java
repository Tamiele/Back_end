package it.epicode.pt_webApp.programmi;

import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.cliente.ClienteRepository;
import it.epicode.pt_webApp.esercizi.ExerciseDTO;
import it.epicode.pt_webApp.week.Week;
import it.epicode.pt_webApp.week.WeekDTO;
import it.epicode.pt_webApp.week.WeekRepository;
import it.epicode.pt_webApp.workout.Workout;
import it.epicode.pt_webApp.workout.WorkoutDTO;
import it.epicode.pt_webApp.workout.WorkoutRepository;
import it.epicode.pt_webApp.workout_exercise.WorkoutExercise;
import it.epicode.pt_webApp.workout_exercise.WorkoutExerciseDTO;
import it.epicode.pt_webApp.workout_exercise.WorkoutExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private WeekRepository weekRepository;

    @Autowired
    private WorkoutExerciseRepository workoutExerciseRepository;

    // Crea un nuovo Program
    public Program createProgram(Program program) {
        return programRepository.save(program);
    }

    // Recupera un Program tramite il suo id
    public Optional<Program> getProgramById(Long id) {
        return programRepository.findById(id);
    }

    // Recupera tutti i Program associati ad un determinato PersonalTrainer
    public List<Program> getProgramsByPersonalTrainer(Long personalTrainerId) {
        return programRepository.findByPersonalTrainerId(personalTrainerId);
    }

    // Aggiorna un Program esistente
    public Program updateProgram(Long id, Program newProgram) {
        return programRepository.findById(id)
                .map(program -> {
                    program.setName(newProgram.getName());
                    program.setDescription(newProgram.getDescription());
                    program.setTemplate(newProgram.isTemplate());
                    program.setAssigned(newProgram.isAssigned());

                    return programRepository.save(program);
                }).orElseThrow(() -> new RuntimeException("Programma non trovato!"));
    }

    // Elimina un Program per id
    public void deleteProgram(Long id) {
        programRepository.deleteById(id);
    }


    public ProgramResponseDTO getProgramStructure(Long programId) {
        // 1. Recupera il Program di base
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programma non trovato"));

        // 2. Mappa i dati base nel DTO
        ProgramResponseDTO dto = new ProgramResponseDTO();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setDescription(program.getDescription());
        dto.setTemplate(program.isTemplate());
        dto.setAssigned(program.isAssigned());

        // 3. Recupera le Week per questo Program
        List<Week> weeks = weekRepository.findByProgramId(programId);
        List<WeekDTO> weekDTOs = weeks.stream().map(week -> {
            WeekDTO weekDTO = new WeekDTO();
            weekDTO.setId(week.getId());
            weekDTO.setWeekNumber(week.getWeekNumber());

            // 4. Per ogni Week, recupera i Workout
            List<Workout> workouts = workoutRepository.findByWeekId(week.getId());
            List<WorkoutDTO> workoutDTOs = workouts.stream().map(workout -> {
                WorkoutDTO workoutDTO = new WorkoutDTO();
                workoutDTO.setId(workout.getId());
                workoutDTO.setName(workout.getName());
                workoutDTO.setCompleted(workout.isCompleted());
                workoutDTO.setDayOfWeek(workout.getDayOfWeek());

                // 5. Per ogni Workout, recupera i WorkoutExercise (cioè gli esercizi)
                List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findByWorkoutId(workout.getId());
                List<WorkoutExerciseDTO> exerciseDTOs = workoutExercises.stream().map(we -> {
                    WorkoutExerciseDTO weDTO = new WorkoutExerciseDTO();
                    weDTO.setId(we.getId());
                    weDTO.setSets(we.getSets());
                    weDTO.setReps(we.getReps());
                    weDTO.setRestType(we.getRestType());
                    weDTO.setRestValue(we.getRestValue());
                    weDTO.setWeight(we.getWeight());
                    // Mappa anche l'esercizio
                    if (we.getExercise() != null) {
                        ExerciseDTO exDTO = new ExerciseDTO();
                        exDTO.setId(we.getExercise().getId());
                        exDTO.setName(we.getExercise().getName());
                        exDTO.setNameEn(we.getExercise().getNameEn());
                        exDTO.setMuscleGroup(we.getExercise().getMuscleGroup());
                        exDTO.setDescription(we.getExercise().getDescription());
                        exDTO.setDescriptionEn(we.getExercise().getDescriptionEn());
                        weDTO.setExercise(exDTO);
                    }
                    return weDTO;
                }).collect(Collectors.toList());
                workoutDTO.setExercises(exerciseDTOs);
                return workoutDTO;
            }).collect(Collectors.toList());
            weekDTO.setWorkouts(workoutDTOs);
            return weekDTO;
        }).collect(Collectors.toList());

        dto.setWeeks(weekDTOs);
        return dto;
    }

    public Program assignProgramToClient(Long programId, Long clientId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programma non trovato"));
        Cliente client = clienteRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato"));
        if (program.getAssignedClients() == null) {
            program.setAssignedClients(new ArrayList<>());
        }
        program.getAssignedClients().add(client);
        return programRepository.save(program);
    }


    // Metodo per recuperare i programmi assegnati ad un cliente
    public List<Program> getProgramsByClient(Long clientId) {
        return programRepository.findByAssignedClientsId(clientId);
    }

    public List<ProgramResponseDTO> getProgramsByClientDTO(Long clientId) {
        List<Program> programs = programRepository.findByAssignedClientsId(clientId);
        return programs.stream()
                .map(program -> ProgramMapper.toDto(program, clientId))
                .collect(Collectors.toList());
    }







}
