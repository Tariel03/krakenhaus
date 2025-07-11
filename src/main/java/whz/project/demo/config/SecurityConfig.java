package whz.project.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import whz.project.demo.enums.Role;
import whz.project.demo.security.BenutzerDetailsService;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final BenutzerDetailsService benutzerDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register/**", "/css/**", "/img/**").permitAll()
                        .requestMatchers("/main").permitAll()
                        .requestMatchers("/arzt", "/arzt/", "/arzt/{id:\\d+}", "/arzt/bewertungen").permitAll()
                        .requestMatchers("/arzt/book-termin").authenticated()
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.toString())
                        .requestMatchers("/termine/**").authenticated()
                        .requestMatchers("/leistungen/**").hasAnyAuthority(Role.ARZT.toString(), Role.ADMIN.toString())
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/main", true)
                        .permitAll())
                .logout(logout -> logout.permitAll())
                .userDetailsService((UserDetailsService) benutzerDetailsService)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }


    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}


