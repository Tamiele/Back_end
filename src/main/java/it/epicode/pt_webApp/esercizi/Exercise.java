package it.epicode.pt_webApp.esercizi;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;
    private String nameEn;
    private String muscleGroup;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String descriptionEn;




}