package ua.kpi.iasa.scback;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.kpi.iasa.scback.controller.dto.UserAdminDTO;
import ua.kpi.iasa.scback.controller.dto.UserDTO;
import ua.kpi.iasa.scback.repository.model.AccountRole;
import ua.kpi.iasa.scback.repository.model.Role;
import ua.kpi.iasa.scback.service.AuthService;

import java.util.List;

@SpringBootApplication
public class ScIasaAppBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScIasaAppBackApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(AuthService authService){
        return args -> {
//            List<Role> allRoles = authService.fetchAllRoles();
//            long id = authService.createStudent(new UserDTO("pass", "admin@admin.com", "Admin", "Cool", null, null));
//            authService.update(id, new UserAdminDTO(false, false, true, true, allRoles), null);
        };
    }
}
