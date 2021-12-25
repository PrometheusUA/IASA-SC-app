package ua.kpi.iasa.scback.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintDTO {
    private String firstname;
    private String surname;
    private String patronymic;
    private String text;
    private int groupId;

    public ComplaintDTO(String firstname,String surname,String patronymic,String text){
        this.firstname = firstname;
        this.surname = surname;
        this.patronymic = patronymic;
        this.text = text;
    }
}
