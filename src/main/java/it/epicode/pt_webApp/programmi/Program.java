package it.epicode.pt_webApp.programmi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.pt_webApp.cliente.Cliente;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import it.epicode.pt_webApp.week.Week;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "programmi")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;


    private boolean isTemplate;

    private boolean isAssigned;


    @ManyToOne
    @JoinColumn(name = "personal_trainer_id")
    private PersonalTrainer personalTrainer;


    @ManyToMany
    @JoinTable(name = "program_assignments",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id"))
    private List<Cliente> assignedClients;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Week> weeks;
}
