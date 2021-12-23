package ua.kpi.iasa.scback.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.iasa.scback.repository.model.Role;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private String title;
    private String imageLink;
    private String text;
    private String link;
}
