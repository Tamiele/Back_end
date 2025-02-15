package it.epicode.pt_webApp.workout;


import it.epicode.pt_webApp.week.Week;
import it.epicode.pt_webApp.week.WeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {

    @Autowired
    private  WorkoutRepository workoutRepository;

    @Autowired
    private WeekRepository weekRepository;

    public Workout addWorkoutToWeek(Long weekId, Workout workout) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new RuntimeException("Week non trovata"));
        workout.setWeek(week);
        return workoutRepository.save(workout);
    }

    public WorkoutDTO convertToDTO(Workout workout) {
        WorkoutDTO dto = new WorkoutDTO();
        dto.setId(workout.getId());
        dto.setName(workout.getName());
        dto.setCompleted(workout.isCompleted());
        dto.setDayOfWeek(workout.getDayOfWeek());


        return dto;
    }
}
