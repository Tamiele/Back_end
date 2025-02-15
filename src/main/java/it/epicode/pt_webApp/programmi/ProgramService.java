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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Transactional
    public void deleteProgram(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Programma non trovato"));


        List<Week> weeks = weekRepository.findByProgramId(programId);
        for (Week week : weeks) {

            List<Workout> workouts = workoutRepository.findByWeekId(week.getId());
            for (Workout workout : workouts) {

                workoutExerciseRepository.deleteByWorkoutId(workout.getId());
                workoutRepository.delete(workout);
            }
            weekRepository.delete(week);
        }


        programRepository.delete(program);
    }

    public Page<ProgramResponseDTO> getMyPrograms(Long trainerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Program> programPage = programRepository.findByPersonalTrainerId(trainerId, pageable);
        return programPage.map(this::getProgramStructure);
    }





    public ProgramResponseDTO getProgramStructure(Program program) {
        ProgramResponseDTO dto = new ProgramResponseDTO();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setDescription(program.getDescription());
        dto.setTemplate(program.isTemplate());
        dto.setAssigned(program.isAssigned());

        List<Week> weeks = weekRepository.findByProgramId(program.getId());
        List<WeekDTO> weekDTOs = weeks.stream().map(week -> {
            WeekDTO weekDTO = new WeekDTO();
            weekDTO.setId(week.getId());
            weekDTO.setWeekNumber(week.getWeekNumber());

            List<Workout> workouts = workoutRepository.findByWeekId(week.getId());
            List<WorkoutDTO> workoutDTOs = workouts.stream().map(workout -> {
                WorkoutDTO workoutDTO = new WorkoutDTO();
                workoutDTO.setId(workout.getId());
                workoutDTO.setName(workout.getName());
                workoutDTO.setCompleted(workout.isCompleted());
                workoutDTO.setDayOfWeek(workout.getDayOfWeek());

                List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findByWorkoutId(workout.getId());
                List<WorkoutExerciseDTO> exerciseDTOs = workoutExercises.stream().map(we -> {
                    WorkoutExerciseDTO weDTO = new WorkoutExerciseDTO();
                    weDTO.setId(we.getId());
                    weDTO.setSets(we.getSets());
                    weDTO.setReps(we.getReps());
                    weDTO.setRestType(we.getRestType());
                    weDTO.setRestValue(we.getRestValue());
                    weDTO.setWeight(we.getWeight());

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
                .orElseThrow(() -> new EntityNotFoundException("Programma non trovato"));
        Cliente client = clienteRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente non trovato"));
        if (program.getAssignedClients() == null) {
            program.setAssignedClients(new ArrayList<>());
        }
        if (!program.getAssignedClients().contains(client)) {
            program.getAssignedClients().add(client);

            program.setAssigned(true);
        }
        return programRepository.save(program);
    }










}
