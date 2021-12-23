package ua.kpi.iasa.scback.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.scback.controller.dto.NewsDTO;
import ua.kpi.iasa.scback.repository.AccountRepo;
import ua.kpi.iasa.scback.repository.NewsRepo;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.News;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NewsService {
    private NewsRepo newsRepo;
    private AccountRepo accountRepo;

    public List<News> fetchAll() {
        return newsRepo.findByDeleted(false);
    }

    public List<News> fetchPage(int pageNum, int len) {
        return newsRepo.findByDeleted(false, PageRequest.of(pageNum, len, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream().toList();
    }

    public News fetchById(long id) {
        final Optional<News> foundNews = newsRepo.findById(id);
        if (foundNews.isEmpty())
            throw new IllegalArgumentException("News not found");
        return foundNews.get();
    }

    public long create(NewsDTO newsDTO, long accId){
         Account acc = accountRepo.getById(accId);
         News news = newsRepo.save(new News(newsDTO, acc));
         return news.getId();
    }

    public void update(long id, NewsDTO newsDTO){
        News news = fetchById(id);
        if (newsDTO.getLink() != null && !newsDTO.getLink().isBlank()) news.setLink(newsDTO.getLink());
        if (newsDTO.getImageLink() != null && !newsDTO.getImageLink().isBlank()) news.setImageLink(newsDTO.getImageLink());
        if (newsDTO.getText() != null && !newsDTO.getText().isBlank()) news.setText(newsDTO.getText());
        if (newsDTO.getTitle() != null && !newsDTO.getTitle().isBlank()) news.setTitle(newsDTO.getTitle());
        newsRepo.save(news);
    }

    public void delete(long id) {
        newsRepo.deleteById(id);
    }

    public void deleteByUser(long id) {
        News news = newsRepo.getById(id);
        news.setDeleted(true);
        newsRepo.save(news);
    }


}
