package ua.kpi.iasa.scback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.scback.model.Account;
import ua.kpi.iasa.scback.service.AuthService;
import ua.kpi.iasa.scback.service.RedisService;
import ua.kpi.iasa.scback.component.EmailServiceComponent;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RedisService redisService;
    private final EmailServiceComponent emailServiceComponent;
    //private final AuthService authService;

    public AuthController(RedisService redisService, EmailServiceComponent emailServiceComponent){ //, AuthService authService){
        this.redisService = redisService;
        this.emailServiceComponent = emailServiceComponent;
        //this.authService = authService;
    }

    @PostMapping(value="/sign-up")
    private ResponseEntity<String> signUp(@RequestBody Account account){
        // check if not exist in Postgres
        // add to database
        String uuid = UUID.randomUUID().toString();
        String response = redisService.set(uuid, account.getEmail(), 60*60*3);
        emailServiceComponent.sendSimpleMessage(account.getEmail(),
                "Confirmation letter on IASA SC",
                "Please, confirm your email via http://localhost:8080/auth/confirm-email/" + uuid +"\nIt will expire in 3 hours.");
        return ResponseEntity.ok(uuid);
    }

    @GetMapping(value = "/confirm-email/{uuid}")
    private ResponseEntity<String> confirmEmail(@PathVariable String uuid){
        try {
            String email = redisService.get(uuid);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        if(true){ // email exists in Postgres database
            try{
                String response = redisService.getAndDelete(uuid);
                //confirm account in Postgres
                return ResponseEntity.ok(response);
            }
            catch (Exception e){
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
