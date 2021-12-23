package ua.kpi.iasa.scback.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.scback.repository.model.Role;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String password;
    private String email;
    private String name;
    private String surname;
    private String patronymic;
    private Collection<Role> roles;
}
