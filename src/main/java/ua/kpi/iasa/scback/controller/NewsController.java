package ua.kpi.iasa.scback.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.scback.component.EmailServiceComponent;
import ua.kpi.iasa.scback.controller.dto.NewsBackDTO;
import ua.kpi.iasa.scback.controller.dto.NewsDTO;
import ua.kpi.iasa.scback.controller.dto.UserBackDTO;
import ua.kpi.iasa.scback.controller.dto.UserDTO;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.AccountRole;
import ua.kpi.iasa.scback.repository.model.News;
import ua.kpi.iasa.scback.security.utility.TokenUtility;
import ua.kpi.iasa.scback.service.AuthService;
import ua.kpi.iasa.scback.service.NewsService;
import ua.kpi.iasa.scback.service.RedisService;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/news")
public class NewsController {
    private NewsService newsService;
    private AuthService authService;

    public NewsController(NewsService newsService, AuthService authService) {
        this.newsService = newsService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<NewsBackDTO>> index(@RequestParam(required = false) Integer pagenum, @RequestParam(required = false) Integer pagelen) {
        if (pagelen == null && pagenum == null) {
            return ResponseEntity.ok(newsService.fetchAll().stream().map(news -> {
                return new NewsBackDTO(news);
            }).collect(Collectors.toList()));
        }
        int _pagelen = pagelen == null ? 12 : pagelen.intValue();
        int _pagenum = pagenum == null ? 0 : pagenum.intValue();

        return ResponseEntity.ok(newsService.fetchPage(_pagenum, _pagelen).stream().map(news -> {
            return new NewsBackDTO(news);
        }).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsBackDTO> indexById(@PathVariable long id) {
        try {
            final News foundNews = newsService.fetchById(id);
            return ResponseEntity.ok(new NewsBackDTO(foundNews));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewsDTO newsDTO, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account creator = authService.fetchByEmail(email);
            long id = newsService.create(newsDTO, creator.getId());
            final String location = String.format("/news/%d", id);
            return ResponseEntity.created(URI.create(location)).build();
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody NewsDTO newsDTO, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account editor = authService.fetchByEmail(email);
            News oldNews = newsService.fetchById(id);
            if (editor.getAccountRoles().stream().map(AccountRole::getRole).toList().contains(authService.fetchRoleByName("Admin")) ||
                    editor == oldNews.getCreatedBy()) {
                newsService.update(id, newsDTO);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to edit this news");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<?> userdelete(@PathVariable long id, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account editor = authService.fetchByEmail(email);
            News oldNews = newsService.fetchById(id);
            if (editor.getAccountRoles().stream().map(AccountRole::getRole).toList().contains(authService.fetchRoleByName("Admin")) ||
                    editor == oldNews.getCreatedBy()) {
                newsService.deleteByUser(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to delete this news");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account editor = authService.fetchByEmail(email);
            News oldNews = newsService.fetchById(id);
            if (editor.getAccountRoles().stream().map(AccountRole::getRole).toList().contains(authService.fetchRoleByName("Admin")) ||
                    editor == oldNews.getCreatedBy()) {
                newsService.delete(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to delete this news");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
