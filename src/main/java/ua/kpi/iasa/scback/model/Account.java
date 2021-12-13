package ua.kpi.iasa.scback.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@Table(name = "accounts")
public class Account {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long acc_id;
    private String email;
    private String firstname;
    private String surname;
    private String patronymic;
    private Long group_id;
    private Boolean iasateka_request;
    private String doc_photo;
}
