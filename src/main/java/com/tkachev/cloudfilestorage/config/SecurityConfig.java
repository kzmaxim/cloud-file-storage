package com.tkachev.cloudfilestorage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkachev.cloudfilestorage.dto.ErrorDTO;
import com.tkachev.cloudfilestorage.dto.SuccessResponseDTO;
import com.tkachev.cloudfilestorage.security.JsonFailureAuthHandler;
import com.tkachev.cloudfilestorage.security.JsonSuccessAuthHandler;
import com.tkachev.cloudfilestorage.security.JsonUsernamePasswordAuthenticationFilter;
import com.tkachev.cloudfilestorage.services.PersonDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl("/api/auth/sign-in");
        filter.setAuthenticationSuccessHandler(new JsonSuccessAuthHandler(objectMapper));
        filter.setAuthenticationFailureHandler(new JsonFailureAuthHandler(objectMapper));

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().hasAnyRole("ADMIN", "USER")
                )
                .addFilterAt(filter, JsonUsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/auth/sign-out")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication == null) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                ErrorDTO errorDTO = new ErrorDTO("Not authenticated");
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(objectMapper.writeValueAsString(errorDTO));
                            } else {
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                                response.getWriter().write("");
                            }
                        })
                ).cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(daoAuthenticationProvider());
        return authBuilder.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(personDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}