package ua.kpi.iasa.scback.repository.model;

import ua.kpi.iasa.scback.controller.dto.ComplaintDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="comp_id")
    private long id;
    @Column
    private String firstname;
    @Column
    private String surname;
    @Column
    private String patronymic;
    @Column
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="processed_by")
    private Account processedBy;
    @Column(name="created_at")
    private Timestamp createdAt;

    public Complaint() {
    }

    public Complaint(String firstname, String surname, String patronymic, String text) {
        this.firstname = firstname;
        this.surname = surname;
        this.patronymic = patronymic;
        this.text = text;
        this.processedBy = null;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Complaint(ComplaintDTO complaint) {
        this.firstname = complaint.getFirstname();
        this.surname = complaint.getSurname();
        this.patronymic = complaint.getPatronymic();
        this.text = complaint.getText();
        this.processedBy = null;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Account getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Account processedBy) {
        this.processedBy = processedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
