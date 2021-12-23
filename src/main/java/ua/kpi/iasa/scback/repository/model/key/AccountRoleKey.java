package ua.kpi.iasa.scback.repository.model.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRoleKey implements Serializable {
    @Column(name="acc_id")
    private long accId;

    @Column(name="role_id")
    private int roleId;
}
