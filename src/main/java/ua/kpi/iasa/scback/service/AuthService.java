package ua.kpi.iasa.scback.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.scback.controller.dto.UserAdminDTO;
import ua.kpi.iasa.scback.controller.dto.UserDTO;
import ua.kpi.iasa.scback.repository.AccountRoleRepo;
import ua.kpi.iasa.scback.repository.RoleRepo;
import ua.kpi.iasa.scback.repository.AccountRepo;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.AccountRole;
import ua.kpi.iasa.scback.repository.model.Role;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService{
    private final AccountRepo accountRepo;
    private final RoleRepo roleRepo;
    private final AccountRoleRepo accountRoleRepo;
    private final PasswordEncoder passwordEncoder;

    public List<Account> fetchAll() {
        return accountRepo.findAll();
    }

    public List<Account> fetchDeleted() {
        return accountRepo.findAllDeleted();
    }

    public List<Account> fetchBlocked() {
        return accountRepo.findAllBlocked();
    }

    public List<Account> fetchUnconfirmed() {
        return accountRepo.findAllUnconfirmed();
    }

    public List<Account> fetchNormal() {
        return accountRepo.findAllNormal();
    }

    public List<Account> fetchByFilter(boolean deleted, boolean confirmed, boolean emailConfirmed, boolean blocked) {
        return accountRepo.findAllMatching(Boolean.valueOf(deleted), Boolean.valueOf(confirmed),
                Boolean.valueOf(emailConfirmed),Boolean.valueOf(blocked));
    }

    public Account fetchById(long id) {
        final Optional<Account> foundUser = accountRepo.findById(id);
        if (foundUser.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundUser.get();
    }

    public Account fetchByEmail(String email) {
        final Optional<Account> foundUser = accountRepo.findByEmail(email);
        if (foundUser.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundUser.get();
    }

    public long createStudent(UserDTO userdto) {
        Account savedUser = new Account();
        userdto.setPassword(passwordEncoder.encode(userdto.getPassword()));
        userdto.setEmail(userdto.getEmail().toLowerCase(Locale.ROOT));
        if (userdto.getRoles() == null || userdto.getRoles().isEmpty()) {
            final Optional<Role> foundStudrole = roleRepo.findByRoleName("Student");
            Role studrole;
            if (foundStudrole.isEmpty())
                studrole = roleRepo.save(new Role("Student"));
            else
                studrole = foundStudrole.get();
            final Account user = new Account(userdto);
            savedUser = accountRepo.save(user);
            final AccountRole studAccRole = new AccountRole(savedUser, studrole, new Timestamp(System.currentTimeMillis()));
            final AccountRole accStudrole = accountRoleRepo.save(studAccRole);
            savedUser.addRole(studAccRole);
            savedUser = accountRepo.save(savedUser);

        }
        else{
            throw new IllegalArgumentException("Student can't be created with roles");
        }
        return savedUser.getId();
    }

    public void addRoleToUser(long id, Role addedRole, long granterId){
//        final Optional<Role> foundRole = roleRepo.findByRoleName(roleName);
//        Role addedRole;
//        if (foundRole.isEmpty())
//            addedRole = roleRepo.save(new Role(roleName));
//        else
//            addedRole = foundRole.get();
        final Account userToAddRole = fetchById(id);
        final Account roleGranter = fetchById(id);
        final AccountRole addedAccRole = accountRoleRepo.save(new AccountRole(userToAddRole, addedRole, new Timestamp(System.currentTimeMillis()), roleGranter));
        userToAddRole.addRole(addedAccRole);
        accountRepo.save(userToAddRole);
    }

    public void update(long id, UserDTO user) throws IllegalAccessException{
        final Account foundUser = fetchById(id);

        if (user.getEmail() != null && !user.getEmail().isBlank() && !user.getEmail().equals(foundUser.getEmail()))
            throw new IllegalAccessException("Email can't be changed");
        // add hashing
        if (user.getPassword() != null && !user.getPassword().isBlank()) foundUser.setPassword_hashed(user.getPassword());
        if (user.getName() != null && !user.getName().isBlank()) foundUser.setFirstname(user.getName());
        if (user.getSurname() != null && !user.getSurname().isBlank()) foundUser.setSurname(user.getSurname());
        if (user.getPatronymic() != null && !user.getPatronymic().isBlank()) foundUser.setPatronymic(user.getPatronymic());
        if (user.getRoles() != null && !user.getRoles().equals(
                foundUser.getAccountRoles().stream().map(AccountRole::getAccount))){
            throw new IllegalAccessException("Roles can't be changed by user");
            //foundUser.setAccountRoles(user.getRoles()); // doesn't work
        }

        accountRepo.save(foundUser);
    }

    public void update(long id, UserAdminDTO user, Account granter) throws IllegalAccessException{
        final Account foundUser = fetchById(id);

        if (user.isBlocked() != foundUser.isBlocked()) foundUser.setBlocked(user.isBlocked());
        if (user.isDeleted() != foundUser.isDeleted()) foundUser.setDeleted(user.isDeleted());
        if (user.isConfirmed() != foundUser.isConfirmed()) foundUser.setConfirmed(user.isConfirmed());
        Stream<Role> foundUserRoles = foundUser.getAccountRoles().stream().map(AccountRole::getRole);
        List<Role> listFoundUserRoles = foundUserRoles.toList();
        if (user.getRoles() != null && !user.getRoles().equals(listFoundUserRoles)) {
            Set<Role> existingRoles = new HashSet<>();
            for(AccountRole r: foundUser.getAccountRoles()){
                if(!user.getRoles().contains(r.getRole())){
                    accountRoleRepo.delete(r);
                    foundUser.deleteRole(r);
                }
                else{
                    existingRoles.add(r.getRole());
                }
            }
            for(Role r: user.getRoles()){
                if(!existingRoles.contains(r)){
                    final AccountRole addedAccRole = accountRoleRepo.save(new AccountRole(foundUser, r, new Timestamp(System.currentTimeMillis()), granter));
                    foundUser.addRole(addedAccRole);
                }
            }
        }

        accountRepo.save(foundUser);
    }

    public void delete(long id) {
        accountRepo.deleteById(id);
    }

    public List<Role> fetchAllRoles() {
        return roleRepo.findAll();
    }

    public Role fetchRoleById(int id) {
        final Optional<Role> foundRole = roleRepo.findById(id);
        if (foundRole.isEmpty())
            throw new IllegalArgumentException("User not found");
        return foundRole.get();
    }

    public long createRole(String roleName) {
        final Role role = new Role(roleName);
        final Role savedRole = roleRepo.save(role);
        return savedRole.getRoleId();
    }

    public void updateRole(int id, String name) throws IllegalAccessException{
        final Role foundRole = fetchRoleById(id);

        // add hashing
        if (name != null && !name.isBlank()) foundRole.setRoleName(name);

        roleRepo.save(foundRole);
    }

    public void deleteRole(int id) {
        roleRepo.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> founduser = accountRepo.findByEmail(username);
        if (founduser.isEmpty()){
            throw new UsernameNotFoundException("There isn't user with such email");
        }
        else{
            Account user = founduser.get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getAccountRoles().forEach(role -> {authorities.add(new SimpleGrantedAuthority(role.getRole().getRoleName()));});
            return new User(user.getEmail(), user.getPassword_hashed(), authorities);
        }
    }
}
