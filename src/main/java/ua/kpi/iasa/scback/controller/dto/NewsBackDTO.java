package ua.kpi.iasa.scback.controller.dto;

import lombok.Data;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.News;

import java.sql.Timestamp;

@Data
public class NewsBackDTO {
    private long id;
    private String title;
    private String imageLink;
    private String text;
    private String link;
    private String author;
    private Timestamp createdAt;

    public NewsBackDTO(News news){
        this.id = news.getId();
        this.imageLink = news.getImageLink();
        this.text = news.getText();
        this.link = news.getLink();
        this.title = news.getTitle();
        this.createdAt = news.getCreatedAt();
        Account creator = news.getCreatedBy();
        this.author = creator.getSurname() + " " + creator.getFirstname() + (creator.getPatronymic() == null?"":(" " + creator.getPatronymic()));
    }
}
