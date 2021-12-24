package ua.kpi.iasa.scback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.scback.repository.model.Complaint;

import java.util.List;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Long> {
    public List<Complaint> findByProcessedByNull();
    public List<Complaint> findByProcessedByNotNull();
}
