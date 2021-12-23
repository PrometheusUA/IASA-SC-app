package ua.kpi.iasa.scback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.scback.repository.model.AccountRole;

@Repository
public interface AccountRoleRepo extends JpaRepository<AccountRole, Long> {
}
