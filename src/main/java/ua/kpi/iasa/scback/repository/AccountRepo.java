package ua.kpi.iasa.scback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.scback.repository.model.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    @Query("SELECT u FROM Account u WHERE u.blocked = true")
    List<Account> findAllBlocked();

    @Query("SELECT u FROM Account u WHERE u.deleted = true")
    List<Account> findAllDeleted();

    @Query("SELECT u FROM Account u WHERE u.confirmed = false")
    List<Account> findAllUnconfirmed();

    @Query("SELECT u FROM Account u WHERE u.deleted = ?1 AND u.confirmed = ?2 AND u.emailConfirmed = ?3 AND u.blocked = ?4")
    List<Account> findAllMatching(Boolean deleted, Boolean confirmed, Boolean emailConfirmed, Boolean blocked);

    @Query("SELECT a FROM Account a WHERE a.blocked = false AND a.deleted = false AND a.confirmed = true")
    List<Account> findAllNormal();

    Optional<Account> findByEmail(String email);
}
