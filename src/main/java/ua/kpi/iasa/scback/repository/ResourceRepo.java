package ua.kpi.iasa.scback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.scback.repository.model.Resource;

import java.util.List;

@Repository
public interface ResourceRepo extends JpaRepository<Resource, Long> {
    public List<Resource> findByOutdated(boolean outdated);
}
