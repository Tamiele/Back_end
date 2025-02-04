package it.epicode.pt_webApp.programmi;

import it.epicode.pt_webApp.cliente.ClienteDTO;
import it.epicode.pt_webApp.personal_trainer.PersonalTrainerDTO;
import it.epicode.pt_webApp.week.WeekDTO;
import lombok.Data;
import java.util.List;

@Data
public class ProgramResponseDTO {
    private Long id;
    private String name;
    private String description;
    private boolean template;
    private boolean assigned;
    private PersonalTrainerDTO personalTrainer;
    private List<WeekDTO> weeks;
}
