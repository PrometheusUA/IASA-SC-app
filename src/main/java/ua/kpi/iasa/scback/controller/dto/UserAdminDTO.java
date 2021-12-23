package ua.kpi.iasa.scback.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.scback.repository.model.Role;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserAdminDTO {
    private boolean blocked;
    private boolean deleted;
    private boolean confirmed;
    private boolean emailConfirmed;
    private Collection<Role> roles;
}
