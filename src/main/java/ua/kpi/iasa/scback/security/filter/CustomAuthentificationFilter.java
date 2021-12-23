package ua.kpi.iasa.scback.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.kpi.iasa.scback.security.filter.request.LoginRequest;
import ua.kpi.iasa.scback.security.utility.TokenUtility;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthentificationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private String jsonUsername;
    private String jsonPassword;

    public CustomAuthentificationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.jsonPassword;
        }else{
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request){
        String username = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.jsonUsername;
        }else{
            username = super.obtainUsername(request);
        }

        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if ("application/json".equals(request.getContentType())) {
            try {
                StringBuffer sb = new StringBuffer();
                String line = null;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                LoginRequest loginRequest = mapper.readValue(sb.toString(), LoginRequest.class);

                this.jsonUsername = loginRequest.getEmail();
                this.jsonPassword = loginRequest.getPassword();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(this.jsonUsername, this.jsonPassword);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User)authentication.getPrincipal();
        TokenUtility tokenUtility = new TokenUtility(user.getUsername(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        Map<String, String> tokens = tokenUtility.generateTokens(request.getRequestURI());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
