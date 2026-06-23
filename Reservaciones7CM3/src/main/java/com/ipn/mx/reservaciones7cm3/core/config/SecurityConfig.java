package com.ipn.mx.reservaciones7cm3.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos de Swagger/Docs
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Autenticación pública
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Enviar correo de prueba
                        .requestMatchers("/api/v1/mail/send-test").permitAll()
                        // Archivos
                        .requestMatchers("/apiArchivos/v1/archivos/**").permitAll()
                        // Reglas para Cuartos
                        .requestMatchers(HttpMethod.GET, "/api/v1/cuartos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuartos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cuartos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cuartos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cuartos/**").hasRole("ADMIN")
                        // Reglas para Reservaciones
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservaciones").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservaciones/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservaciones/usuario/{usuarioId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservaciones/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/reservaciones/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reservaciones/**").authenticated()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173", // React / Vite
                "http://localhost:3000", // Next.js / React
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
