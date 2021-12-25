package ua.kpi.iasa.scback.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.scback.repository.model.Account;

@Data
@NoArgsConstructor
public class UserBackDTO {
    public UserBackDTO(Account user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.surname = user.getSurname();
        this.patronymic = user.getPatronymic();
        this.docPhoto = user.getDocPhoto();
    }

    private long id;
    private String email;
    private String firstname;
    private String surname;
    private String patronymic;
    private String docPhoto;
}
