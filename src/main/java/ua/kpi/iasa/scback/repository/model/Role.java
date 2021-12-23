package ua.kpi.iasa.scback.repository.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name="roles")
public final class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="role_id")
    private int roleId;
    @Column(name="role_name")
    private String roleName;
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private Collection<AccountRole> accounts;

    public Role(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Role() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleId() {
        return roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return roleId == role.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
