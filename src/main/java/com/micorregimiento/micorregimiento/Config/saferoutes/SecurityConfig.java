package com.micorregimiento.micorregimiento.Config.saferoutes;

import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private IJwtService jwtService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS con configuración corregida
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Deshabilitar CSRF para APIs REST
                .csrf(csrf -> csrf.disable())

                // Configuración de sesiones (stateless para JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configuración de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/v1/login/**").permitAll()
                        .requestMatchers("/api/v1/register/public").permitAll()
                        .requestMatchers("/api/v1/register/roles").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/v1/publicaciones/all").permitAll()
                        // Endpoints de Swagger y OpenAPI (públicos)
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()


                        // Endpoints que requieren autenticación
                        .requestMatchers("/api/v1/register/admin").authenticated()
                        .requestMatchers("/api/v1/publicaciones/**").authenticated()
                        .requestMatchers("/api/v1/admin/**").authenticated()
                        .anyRequest().authenticated()
                )

                // Agregar filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
