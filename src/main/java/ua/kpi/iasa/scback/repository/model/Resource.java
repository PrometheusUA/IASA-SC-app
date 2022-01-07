package ua.kpi.iasa.scback.repository.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "resourses_temp")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="resourse_id")
    private long id;
    @Column
    private String teacher;
    @Column
    private String discipline;
    @Column(name="additional_info")
    private String additionalInfo;
    @Column
    private String link;
    @Column(name = "added_date")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="added_by")
    private Account createdBy;
    @Column(name = "is_outdated")
    private boolean outdated;

    public Resource() { }

    public Resource(long id, String teacher, String discipline, String additionalInfo, String link, Timestamp createdAt, Account createdBy, boolean outdated) {
        this.id = id;
        this.teacher = teacher;
        this.discipline = discipline;
        this.additionalInfo = additionalInfo;
        this.link = link;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.outdated = outdated;
    }

    public Resource(String teacher, String discipline, String additionalInfo, String link, Account createdBy) {
        this.teacher = teacher;
        this.discipline = discipline;
        this.additionalInfo = additionalInfo;
        this.link = link;
        this.createdBy = createdBy;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.outdated = false;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    public long getId() {
        return id;
    }
}
