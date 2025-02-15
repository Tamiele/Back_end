package it.epicode.pt_webApp.programmi;

import lombok.Data;

@Data
public class ProgramRequestDTO {
    private String name;
    private String description;
    private boolean template;

}
