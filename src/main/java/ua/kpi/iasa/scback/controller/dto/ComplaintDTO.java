package ua.kpi.iasa.scback.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComplaintDTO {
    private String firstname;
    private String surname;
    private String patronymic;
    private String text;
    private int groupId;
}
