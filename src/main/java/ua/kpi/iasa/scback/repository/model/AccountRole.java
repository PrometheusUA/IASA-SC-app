package ua.kpi.iasa.scback.repository.model;

import ua.kpi.iasa.scback.repository.model.key.AccountRoleKey;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name="account_roles")
public class AccountRole {
    @EmbeddedId
    private AccountRoleKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accId")
    @JoinColumn(name="acc_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("roleId")
    @JoinColumn(name="role_id")
    private Role role;

    @Column(name="grant_date")
    private Timestamp grantDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="grant_by")
    private Account granter;

//    @Column(name="corresp_id")
//    private long correspId;
    //implement teacher and student when ready

    public AccountRole(){}

    public AccountRole(Account account, Role role, Timestamp grantDate) {
        this.id = new AccountRoleKey(account.getId(), role.getRoleId());
        this.account = account;
        this.role = role;
        this.grantDate = grantDate;
    }

    public AccountRole(Account account, Role role, Timestamp grantDate, Account granter) {
        this.id = new AccountRoleKey(account.getId(), role.getRoleId());
        this.account = account;
        this.role = role;
        this.grantDate = grantDate;
        this.granter = granter;
    }

    public AccountRoleKey getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Timestamp getGrantDate() {
        return grantDate;
    }

    public void setGrantDate(Timestamp grantDate) {
        this.grantDate = grantDate;
    }

    public Account getGranter() {
        return granter;
    }

    public void setGranter(Account granter) {
        this.granter = granter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRole that = (AccountRole) o;
        return id.equals(that.id) && account.equals(that.account) && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, account, role);
    }
}
