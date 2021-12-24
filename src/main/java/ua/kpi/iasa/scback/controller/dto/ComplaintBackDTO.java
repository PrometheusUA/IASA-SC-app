package ua.kpi.iasa.scback.controller.dto;

import lombok.Data;
import ua.kpi.iasa.scback.repository.model.Complaint;

@Data
public class ComplaintBackDTO {
    private long id;
    private String firstname;
    private String surname;
    private String patronymic;
    private String text;
    private UserBackDTO processedBy;

    public ComplaintBackDTO(Complaint complaint){
        this.id = complaint.getId();
        this.firstname = complaint.getFirstname();
        this.surname = complaint.getSurname();
        this.patronymic = complaint.getPatronymic();
        this.text = complaint.getText();
        this.processedBy = complaint.getProcessedBy() == null? null:new UserBackDTO(complaint.getProcessedBy());
    }
}
