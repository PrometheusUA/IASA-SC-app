package ua.kpi.iasa.scback.repository.model;

import ua.kpi.iasa.scback.controller.dto.UserDTO;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "accounts")
public final class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="acc_id")
    private long id;
    @Column
    private String password_hashed;
    @Column
    private String email;
    @Column
    private String firstname;
    @Column
    private String surname;
    @Column
    private String patronymic;
    @Column
    private boolean blocked;
    @Column
    private boolean deleted;
    @Column(name="acc_confirmed")
    private boolean confirmed;
    @Column(name="doc_photo")
    private String docPhoto;
    @Column(name="created_on")
    private Timestamp createdOn;
    @Column(name="email_confirmed")
    private boolean emailConfirmed;
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    private Set<AccountRole> accountRoles;
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<News> createdNews;
    @OneToMany(mappedBy = "processedBy", fetch = FetchType.LAZY)
    private Set<Complaint> processedComplaints;
//    @ManyToMany(fetch = FetchType.EAGER)
//    private Collection<Role> roles = new ArrayList<>();

    public Account() {
        this.accountRoles = new HashSet<>();
        this.createdNews = new HashSet<>();
        this.processedComplaints = new HashSet<>();

    }

    public Account(String password_hashed, String email, String firstname, String surname, String patronymic,
                   boolean blocked, boolean deleted, boolean acc_confirmed, String doc_photo, boolean emailConfirmed) {
        this.password_hashed = password_hashed;
        this.email = email;
        this.firstname = firstname;
        this.surname = surname;
        this.patronymic = patronymic;
        this.blocked = blocked;
        this.deleted = deleted;
        this.confirmed = acc_confirmed;
        this.docPhoto = doc_photo;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.emailConfirmed = emailConfirmed;
        this.accountRoles = new HashSet<>();
        this.createdNews = new HashSet<>();
        this.processedComplaints = new HashSet<>();

    }

//    public Account(UserDTO userDTO) {
//        this.password_hashed = userDTO.getPassword();
//        this.email = userDTO.getEmail();
//        this.firstname = userDTO.getName();
//        this.surname = userDTO.getSurname();
//        this.patronymic = userDTO.getPatronymic();
//        this.blocked = false;
//        this.deleted = false;
//        this.confirmed = false;
//        this.docPhoto = null;
//        this.accountRoles.add(studrole);
//        //roles.add(studrole);
//    }

    public Account(UserDTO userDTO) {
        this.password_hashed = userDTO.getPassword();
        this.email = userDTO.getEmail();
        this.firstname = userDTO.getFirstname();
        this.surname = userDTO.getSurname();
        this.patronymic = userDTO.getPatronymic();
        this.blocked = false;
        this.deleted = false;
        this.confirmed = false;
        this.emailConfirmed = false;
        this.docPhoto = null;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.accountRoles = new HashSet<>();
        this.createdNews = new HashSet<>();
        this.processedComplaints = new HashSet<>();

    }

    public Account(UserDTO userDTO, Set<AccountRole> roles){
        this.password_hashed = userDTO.getPassword();
        this.email = userDTO.getEmail();
        this.firstname = userDTO.getFirstname();
        this.surname = userDTO.getSurname();
        this.patronymic = userDTO.getPatronymic();
        this.blocked = false;
        this.deleted = false;
        this.confirmed = false;
        emailConfirmed = false;
        this.docPhoto = null;
        this.accountRoles = roles;
        this.createdOn = new Timestamp(System.currentTimeMillis());
        this.createdNews = new HashSet<>();
        this.processedComplaints = new HashSet<>();

    }

    public long getId() {
        return id;
    }

    public String getPassword_hashed() {
        return password_hashed;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setPassword_hashed(String password_hashed) {
        this.password_hashed = password_hashed;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void addRole(AccountRole newRole) {
        if (accountRoles.contains(newRole))
            throw new IllegalArgumentException("User allready has this role");
        accountRoles.add(newRole);
    }

    public void deleteRole(AccountRole roleToDelete) {
        if (!accountRoles.contains(roleToDelete))
            throw new IllegalArgumentException("User hasn't this role");
        accountRoles.remove(roleToDelete);
    }

    public Set<AccountRole> getAccountRoles() {
//        List<Role> trueRoles = new ArrayList<>();
//        accountRoles.stream().forEach(accountRole -> {
//            trueRoles.add(accountRole.getRole());
//        });
        return accountRoles;
    }

    public void setAccountRoles(Set<AccountRole> roles) {
        this.accountRoles = roles;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getDocPhoto() {
        return docPhoto;
    }

    public void setDocPhoto(String docPhoto) {
        this.docPhoto = docPhoto;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public Set<News> getCreatedNews() {
        return createdNews;
    }

    public void setCreatedNews(Set<News> createdNews) {
        this.createdNews = createdNews;
    }

    public Set<Complaint> getProcessedComplaints() {
        return processedComplaints;
    }

    public void setProcessedComplaints(Set<Complaint> processedComplaints) {
        this.processedComplaints = processedComplaints;
    }
}
