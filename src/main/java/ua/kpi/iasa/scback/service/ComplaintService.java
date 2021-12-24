package ua.kpi.iasa.scback.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.scback.controller.dto.ComplaintDTO;
import ua.kpi.iasa.scback.controller.dto.NewsDTO;
import ua.kpi.iasa.scback.repository.ComplaintRepo;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.Complaint;
import ua.kpi.iasa.scback.repository.model.News;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintService {
    private ComplaintRepo complaintRepo;
    private AuthService authService;

    public List<Complaint> fetchAll() {
        return complaintRepo.findAll();
    }
    public List<Complaint> fetchUnprocessed() { return complaintRepo.findByProcessedByNull(); }
    public List<Complaint> fetchProcessed() { return complaintRepo.findByProcessedByNotNull(); }

    public Complaint fetchById(long id) {
        final Optional<Complaint> foundComplaint = complaintRepo.findById(id);
        if (foundComplaint.isEmpty())
            throw new IllegalArgumentException("News not found");
        return foundComplaint.get();
    }

    public long create(ComplaintDTO complaintDTO){
        Complaint complaint = complaintRepo.save(new Complaint(complaintDTO));
        return complaint.getId();
    }

    public void update(long id, ComplaintDTO complaintDTO){
        Complaint complaint = fetchById(id);
        if (complaintDTO.getText() != null && !complaintDTO.getText().isBlank()) complaint.setText(complaintDTO.getText());
        if (complaintDTO.getPatronymic() != null && !complaintDTO.getPatronymic().isBlank()) complaint.setPatronymic(complaintDTO.getPatronymic());
        if (complaintDTO.getSurname() != null && !complaintDTO.getSurname().isBlank()) complaint.setSurname(complaintDTO.getSurname());
        if (complaintDTO.getFirstname() != null && !complaintDTO.getFirstname().isBlank()) complaint.setFirstname(complaintDTO.getFirstname());
        complaintRepo.save(complaint);
    }

    public void process (long id, Account processedBy){
        Complaint complaint = fetchById(id);
        complaint.setProcessedBy(processedBy);
        complaintRepo.save(complaint);
    }

    public void delete(long id) {
        complaintRepo.deleteById(id);
    }
}
