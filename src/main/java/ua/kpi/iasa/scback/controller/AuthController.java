package ua.kpi.iasa.scback.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.scback.controller.dto.UserAdminDTO;
import ua.kpi.iasa.scback.controller.dto.UserBackDTO;
import ua.kpi.iasa.scback.controller.dto.UserDTO;
import ua.kpi.iasa.scback.repository.model.Account;
import ua.kpi.iasa.scback.repository.model.AccountRole;
import ua.kpi.iasa.scback.repository.model.Role;
import ua.kpi.iasa.scback.security.utility.TokenUtility;
import ua.kpi.iasa.scback.service.AuthService;
import ua.kpi.iasa.scback.service.RedisService;
import ua.kpi.iasa.scback.component.EmailServiceComponent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RedisService redisService;
    private final EmailServiceComponent emailServiceComponent;
    private AuthService authService;


    public AuthController(RedisService redisService, EmailServiceComponent emailServiceComponent, AuthService authService){
        this.redisService = redisService;
        this.emailServiceComponent = emailServiceComponent;
        this.authService = authService;
    }

//    @PostMapping(value="/sign-up")
//    private ResponseEntity<String> signUp(@RequestBody Account account){
//        // check if not exist in Postgres
//        // add to database
//        String uuid = UUID.randomUUID().toString();
//        String response = redisService.set(uuid, account.getEmail(), 60*60*3);
//        emailServiceComponent.sendSimpleMessage(account.getEmail(),
//                "Confirmation letter on IASA SC",
//                "Please, confirm your email via http://localhost:8080/auth/confirm-email/" + uuid +"\nIt will expire in 3 hours.");
//        return ResponseEntity.ok(uuid);
//    }

    @GetMapping(value = "/confirm-email/{uuid}")
    private ResponseEntity<String> confirmEmail(@PathVariable String uuid){
        String email = "";
        try {
            email = redisService.get(uuid);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        try{
            Account user = authService.fetchByEmail(email);
            if(user.isBlocked() || user.isDeleted()){
                return ResponseEntity.status(403).body("User is blocked or deleted");
            }
            String response = redisService.getAndDelete(uuid);
            //confirm account in Postgres
            authService.update(user.getId(), new UserAdminDTO(user.isBlocked(), user.isDeleted(), user.isConfirmed(), true, user.getAccountRoles().stream().map(AccountRole::getRole).toList()), null);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping
    public ResponseEntity<List<UserBackDTO>> index(@RequestParam Optional<Boolean> blocked, @RequestParam Optional<Boolean> deleted,
                                                   @RequestParam Optional<Boolean> confirmed, @RequestParam Optional<Boolean> emailConfirmed) {
        boolean _blocked = blocked.orElse(Boolean.FALSE).booleanValue();
        boolean _deleted = deleted.orElse(Boolean.FALSE).booleanValue();
        boolean _confirmed = confirmed.orElse(Boolean.FALSE).booleanValue();
        boolean _emailConfirmed = emailConfirmed.orElse(Boolean.FALSE).booleanValue();

        List<UserBackDTO> usersToSend = new ArrayList<>();
        List<Account> validUsers;
        if (_deleted && _blocked){
            return ResponseEntity.status(403).build();
        }
        validUsers = authService.fetchByFilter(_deleted, _confirmed, _emailConfirmed, _blocked);
        validUsers.forEach(user -> { usersToSend.add(new UserBackDTO(user));});
        return ResponseEntity.ok(usersToSend);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBackDTO> profile(@PathVariable long id) {
        try {
            final Account foundUser = authService.fetchById(id);
            return ResponseEntity.ok(new UserBackDTO(foundUser));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/signup")
    public void signup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            StringBuffer requestBodyStr = new StringBuffer();
            String requestLine;
            try {
                BufferedReader reader = request.getReader();
                while ((requestLine = reader.readLine()) != null)
                    requestBodyStr.append(requestLine);
            } catch (Exception e) {
                throw new IOException("Error parsing json body");
            }

            JSONObject requestBody;

            try {
                String requestBodyString = requestBodyStr.toString();
                JSONParser parser = new JSONParser();
                requestBody = (JSONObject) parser.parse(requestBodyString);
            } catch (Exception e) {
                throw new IOException("Error parsing JSON request string");
            }

            UserDTO newUser = new UserDTO();
            newUser.setEmail(requestBody.getAsString("email"));
            newUser.setPassword(requestBody.getAsString("password"));
            newUser.setFirstname(requestBody.getAsString("firstname"));
            newUser.setSurname(requestBody.getAsString("surname"));
            newUser.setPatronymic(requestBody.getAsString("patronymic"));

            final long id = authService.createStudent(newUser);
            Account user = authService.fetchById(id);
            TokenUtility tokenUtility = new TokenUtility(user.getEmail(), user.getAccountRoles().stream()
                    .map(AccountRole::getRole).map(Role::getRoleName).collect(Collectors.toList()));

            String uuid = UUID.randomUUID().toString();
            redisService.set(uuid, user.getEmail(), 60*60*3);
            emailServiceComponent.sendSimpleMessage(user.getEmail(),
                    "Confirmation letter on IASA SC",
                    "Please, confirm your email via http://localhost:8080/auth/confirm-email/" + uuid +"\nIt will expire in 3 hours.");

            Map<String, String> tokens = tokenUtility.generateTokens(request.getRequestURI());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
        catch (Exception e){
            response.setStatus(FORBIDDEN.value());
            response.setHeader("error", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> adminUpdate(@PathVariable long id, @RequestBody UserAdminDTO user, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account admin = authService.fetchByEmail(email);
            authService.update(id, user, admin);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
    }

    @PatchMapping
    public ResponseEntity<Void> update(@RequestHeader String authorization, @RequestBody UserDTO user) {
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account oldUser = authService.fetchByEmail(email);
            if (!user.getRoles().equals(oldUser.getAccountRoles())){
                throw new IllegalAccessException("Student can't change roles!");
            }
            authService.update(oldUser.getId(), user);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader String authorization) {
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            String email = decodedJWT.getSubject();
            Account oldUser = authService.fetchByEmail(email);
            UserAdminDTO deleteUser = new UserAdminDTO(false, true, oldUser.isConfirmed(), oldUser.isEmailConfirmed(), oldUser.getAccountRoles().stream().map(AccountRole::getRole).toList());

            authService.update(oldUser.getId(), deleteUser, null);

            return ResponseEntity.noContent().build();
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
        catch (IllegalAccessException e){
            return ResponseEntity.status(403).build();
        }
        catch (RuntimeException e){
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        authService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/role")
    public ResponseEntity<String> createRole(@RequestBody String roleName){
        try{
            long id = authService.createRole(roleName);
            final String location = String.format("/auth/%d", id);
            return ResponseEntity.created(URI.create(location)).body(String.valueOf(id));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/role")
    public ResponseEntity<List<Role>> fetchAllRoles(){
        return ResponseEntity.ok(authService.fetchAllRoles());
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<Role> fetchRoleById(@PathVariable int id){
        try {
            return ResponseEntity.ok(authService.fetchRoleById(id));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorizationHeader);
            String email = decodedJWT.getSubject();
            Account user = authService.fetchByEmail(email);
            TokenUtility tokenUtility = new TokenUtility(email, user.getAccountRoles().stream().map(AccountRole::getRole).map(Role::getRoleName).collect(Collectors.toList()));
            Map<String, String> tokens = tokenUtility.generateAccessToken(request.getRequestURI());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
        catch (Exception e){
            response.setStatus(FORBIDDEN.value());
            response.setHeader("error", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }
}
