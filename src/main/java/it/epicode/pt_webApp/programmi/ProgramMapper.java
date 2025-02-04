package it.epicode.pt_webApp.programmi;

import it.epicode.pt_webApp.cliente.ClienteDTO;
import it.epicode.pt_webApp.esercizi.ExerciseDTO;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerDTO;
import it.epicode.pt_webApp.week.WeekDTO;
import it.epicode.pt_webApp.workout.WorkoutDTO;
import it.epicode.pt_webApp.workout_exercise.WorkoutExerciseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ProgramMapper {


    public static ProgramResponseDTO toDto(Program program) {

        ProgramResponseDTO dto = new ProgramResponseDTO();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setDescription(program.getDescription());
        dto.setTemplate(program.isTemplate());
        dto.setAssigned(program.isAssigned()); // Assicurati di aggiornare questo campo se necessario

        // Mappatura del PersonalTrainer
        PersonalTrainer trainer = program.getPersonalTrainer();
        if (trainer != null) {
            PersonalTrainerDTO trainerDTO = new PersonalTrainerDTO();
            trainerDTO.setId(trainer.getId());
            trainerDTO.setUsername(trainer.getUsername());
            trainerDTO.setEmail(trainer.getEmail());
            trainerDTO.setNome(trainer.getNome());
            trainerDTO.setCognome(trainer.getCognome());
            trainerDTO.setDataDiNascita(trainer.getDataDiNascita());
            // Mappa anche la lista dei clienti (se presente)
            if (trainer.getClienti() != null && !trainer.getClienti().isEmpty()) {
                trainerDTO.setClienti(
                        trainer.getClienti().stream().map(client -> {
                            ClienteDTO clientDTO = new ClienteDTO();
                            clientDTO.setId(client.getId());
                            clientDTO.setUsername(client.getUsername());
                            clientDTO.setEmail(client.getEmail());
                            clientDTO.setNome(client.getNome());
                            clientDTO.setCognome(client.getCognome());
                            clientDTO.setDataDiNascita(client.getDataDiNascita());
                            if (client.getPersonalTrainer() != null) {
                                clientDTO.setPersonalTrainerId(client.getPersonalTrainer().getId().intValue());
                            }
                            return clientDTO;
                        }).collect(Collectors.toList())
                );
            }
            dto.setPersonalTrainer(trainerDTO);
        }

        // Mappatura delle Week e dei relativi Workout
        if (program.getWeeks() != null && !program.getWeeks().isEmpty()) {
            dto.setWeeks(
                    program.getWeeks().stream().map(week -> {
                        WeekDTO weekDTO = new WeekDTO();
                        weekDTO.setId(week.getId());
                        weekDTO.setWeekNumber(week.getWeekNumber());

                        // Mappatura dei Workout per la Week
                        if (week.getWorkouts() != null && !week.getWorkouts().isEmpty()) {
                            weekDTO.setWorkouts(
                                    week.getWorkouts().stream().map(workout -> {
                                        WorkoutDTO workoutDTO = new WorkoutDTO();
                                        workoutDTO.setId(workout.getId());
                                        workoutDTO.setName(workout.getName());
                                        workoutDTO.setCompleted(workout.isCompleted());
                                        workoutDTO.setDayOfWeek(workout.getDayOfWeek());

                                        // Mappatura degli esercizi (workoutExercises) per il Workout
                                        if (workout.getWorkoutExercises() != null && !workout.getWorkoutExercises().isEmpty()) {
                                            workoutDTO.setExercises(
                                                    workout.getWorkoutExercises().stream().map(we -> {
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
                                                    }).collect(Collectors.toList())
                                            );
                                        }
                                        return workoutDTO;
                                    }).collect(Collectors.toList())
                            );
                        }
                        return weekDTO;
                    }).collect(Collectors.toList())
            );
        }
        return dto;
    }

//per la get programmi dei clienti
public static ProgramResponseDTO toDto(Program program, Long loggedClientId) {
    ProgramResponseDTO dto = toDto(program);

    if (dto.getPersonalTrainer() != null && dto.getPersonalTrainer().getClienti() != null) {
        List<ClienteDTO> filtered = dto.getPersonalTrainer().getClienti().stream()
                .filter(client -> client.getId().equals(loggedClientId))
                .collect(Collectors.toList());
        dto.getPersonalTrainer().setClienti(filtered);
    }
    return dto;
}

}
