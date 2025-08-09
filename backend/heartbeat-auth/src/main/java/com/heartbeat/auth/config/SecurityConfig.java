package com.heartbeat.auth.config;

import com.heartbeat.auth.filter.JwtAuthenticationFilter;
import com.heartbeat.auth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/error").permitAll()
                        
                        // Static resources
                        .requestMatchers("/favicon.ico", "/robots.txt").permitAll()
                        
                        // Authentication required for all other endpoints
                        .requestMatchers(HttpMethod.GET, "/patients/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "HELPDESK")
                        .requestMatchers(HttpMethod.POST, "/patients").hasAnyRole("ADMIN", "HELPDESK")
                        .requestMatchers(HttpMethod.PUT, "/patients/**").hasAnyRole("ADMIN", "HELPDESK", "NURSE", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/patients/**").hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/treatments/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/treatments").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/treatments/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/treatments/**").hasAnyRole("ADMIN", "DOCTOR")
                        
                        .requestMatchers(HttpMethod.GET, "/medicines/**").hasAnyRole("ADMIN", "PHARMA_ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/medicines").hasAnyRole("ADMIN", "PHARMA_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/medicines/**").hasAnyRole("ADMIN", "PHARMA_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/medicines/**").hasAnyRole("ADMIN", "PHARMA_ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/lab-tests/**").hasAnyRole("ADMIN", "PHARMA_ADMIN", "LAB_ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/lab-tests").hasAnyRole("ADMIN", "PHARMA_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/lab-tests/**").hasAnyRole("ADMIN", "PHARMA_ADMIN", "LAB_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/lab-tests/**").hasAnyRole("ADMIN", "PHARMA_ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/lab-results/**").hasAnyRole("ADMIN", "LAB_ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/lab-results").hasAnyRole("ADMIN", "LAB_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/lab-results/**").hasAnyRole("ADMIN", "LAB_ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/notifications/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.POST, "/notifications").hasAnyRole("ADMIN", "DOCTOR", "NURSE")
                        .requestMatchers(HttpMethod.PUT, "/notifications/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/notifications/**").hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/files/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "LAB_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/files/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE", "LAB_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/files/**").hasAnyRole("ADMIN", "DOCTOR")
                        
                        .requestMatchers(HttpMethod.GET, "/practice-templates/**").hasAnyRole("ADMIN", "SUPERVISOR", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/practice-templates").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/practice-templates/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/practice-templates/**").hasAnyRole("ADMIN", "SUPERVISOR")
                        
                        .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        
                        .requestMatchers(HttpMethod.GET, "/dashboard/**").authenticated()
                        
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000", "http://localhost:5173", "https://*.heartbeat.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}