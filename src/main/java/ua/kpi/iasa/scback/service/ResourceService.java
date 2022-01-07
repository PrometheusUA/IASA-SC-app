package ua.kpi.iasa.scback.service;

import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.AllArgsConstructor;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.Resource;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.scback.repository.AccountRepo;
import ua.kpi.iasa.scback.repository.ResourceRepo;
import ua.kpi.iasa.scback.repository.model.News;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResourceService {
    private ResourceRepo resourceRepo;
    private AccountRepo accountRepo;

    public List<Resource> fetchAll() {
        return resourceRepo.findAll();
    }
    public List<Resource> fetchAllActual() {
        return resourceRepo.findByOutdated(false);
    }

    public Resource fetchById(long id) {
        final Optional<Resource> foundResource = resourceRepo.findById(id);
        if(foundResource.isEmpty())
            throw new IllegalArgumentException("Resource not found");
        return foundResource.get();
    }

    public long create(long accId, String teacher, String discipline, String additionalInfo, String link){
        Account acc = accountRepo.getById(accId);
        Resource resource = resourceRepo.save(new Resource(teacher, discipline, additionalInfo, link, acc));
        return resource.getId();
    }

    public void delete(long id) {
        resourceRepo.deleteById(id);
    }

}
