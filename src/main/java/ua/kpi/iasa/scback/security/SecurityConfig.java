package ua.kpi.iasa.scback.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ua.kpi.iasa.scback.security.filter.CustomAuthentificationFilter;
import ua.kpi.iasa.scback.security.filter.CustomAuthorizationFilter;
import ua.kpi.iasa.scback.service.AuthService;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthentificationFilter customAuthentificationFilter = new CustomAuthentificationFilter(authenticationManagerBean(), authService);
        customAuthentificationFilter.setFilterProcessesUrl("/auth/signin");
        customAuthentificationFilter.setUsernameParameter("email");
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(POST, "/auth/role").hasAnyAuthority("Admin")
                .antMatchers(DELETE, "/auth/**").hasAnyAuthority("Admin")
                .antMatchers(PATCH, "/auth").hasAnyAuthority("Student")
                .antMatchers(PATCH, "/auth/**").hasAnyAuthority("Admin")
                .antMatchers(POST, "/auth/signin", "/auth/signup").permitAll()
                .antMatchers(GET,"/auth/token/refresh", "/auth/confirm-email/**").permitAll()
                .antMatchers("/auth/**").hasAnyAuthority("Student")
                .antMatchers(GET, "/news/**").permitAll()
                .antMatchers(POST, "/news/**").hasAnyAuthority("Admin", "Elderly", "Teacher")
                .antMatchers(PATCH, "/news/**").hasAnyAuthority("Admin", "Elderly", "Teacher")
                .antMatchers(DELETE, "/news/**").hasAnyAuthority("Admin")
                .antMatchers(POST, "/complaints").permitAll()
                .antMatchers("/complaints/**").hasAnyAuthority("Admin")
                .anyRequest().permitAll();

        http.addFilterBefore(new CustomAuthorizationFilter(authService), UsernamePasswordAuthenticationFilter.class);
        http.addFilter(customAuthentificationFilter);
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3000/", "http://localhost:3000/login/"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS", "DELETE", "PUT", "PATCH"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/*", configuration);
//        source.registerCorsConfiguration("/*/*", configuration);
//        source.registerCorsConfiguration("/*/*/*", configuration);
//
//        //source.registerCorsConfiguration("/auth/signin", configuration);
//        return source;
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
            return super.authenticationManagerBean();
    }
}
