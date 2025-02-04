package it.epicode.pt_webApp.esercizi;

import lombok.Data;

@Data
public class ExerciseDTO {
    private Long id;
    private String name;
    private String nameEn;
    private String muscleGroup;
    private String description;
    private String descriptionEn;
}
