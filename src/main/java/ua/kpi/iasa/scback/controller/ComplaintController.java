package ua.kpi.iasa.scback.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.scback.controller.dto.ComplaintBackDTO;
import ua.kpi.iasa.scback.controller.dto.ComplaintDTO;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.Complaint;
import ua.kpi.iasa.scback.security.utility.TokenUtility;
import ua.kpi.iasa.scback.service.AuthService;
import ua.kpi.iasa.scback.service.ComplaintService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {
    private ComplaintService complaintService;
    private AuthService authService;

    public ComplaintController(ComplaintService complaintService, AuthService authService) {
        this.complaintService = complaintService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<ComplaintBackDTO>> index(@RequestParam(required = false) Boolean processed){
        try{
            List<Complaint> complaints;
            if(processed == null)
                complaints = complaintService.fetchAll();
            else if (processed.booleanValue())
                complaints = complaintService.fetchProcessed();
            else
                complaints = complaintService.fetchUnprocessed();
            return ResponseEntity.ok(complaints.stream().map(complaint -> {
                return new ComplaintBackDTO(complaint);
            }).collect(Collectors.toList()));
        }
        catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintBackDTO> get(@PathVariable long id){
        try{
            Complaint complaint = complaintService.fetchById(id);
            return ResponseEntity.ok(new ComplaintBackDTO(complaint));
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> publish(@RequestBody ComplaintDTO complaintDTO){
        try{
            long id = complaintService.create(complaintDTO);
            final String location = String.format("/complaints/%d", id);
            return ResponseEntity.created(URI.create(location)).build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> process(@PathVariable long id, @RequestHeader String authorization){
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account processor = authService.fetchByEmail(email);
            complaintService.process(id, processor);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        try{
            complaintService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }
}
