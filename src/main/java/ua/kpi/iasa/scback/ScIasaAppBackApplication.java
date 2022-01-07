package ua.kpi.iasa.scback;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ua.kpi.iasa.scback.controller.ResoursesGRPCController;
import ua.kpi.iasa.scback.controller.dto.UserAdminDTO;
import ua.kpi.iasa.scback.controller.dto.UserDTO;
import ua.kpi.iasa.scback.repository.model.AccountRole;
import ua.kpi.iasa.scback.repository.model.Role;
import ua.kpi.iasa.scback.service.AuthService;
import ua.kpi.iasa.scback.service.ResourceService;

import java.util.List;

@SpringBootApplication
@Slf4j
public class ScIasaAppBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScIasaAppBackApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(ResourceService resourceService){
        return args -> {
//            List<Role> allRoles = authService.fetchAllRoles();
//            long id = authService.createStudent(new UserDTO("pass", "admin@admin.com", "Admin", "Cool", null, null));
//            authService.update(id, new UserAdminDTO(false, false, true, true, allRoles), null);
            try {
                Server server = ServerBuilder
                        .forPort(8082)
                        .addService(new ResoursesGRPCController(resourceService)).build();

                server.start();
                server.awaitTermination();
            }
            catch (Exception e){
                log.error("Error starting GRPC server: " + e.getMessage());
            }
        };
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**").allowedOrigins("*");
//            }
//        };
//    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/*").allowedOrigins("*");
//            }
//        };
//    }
}
