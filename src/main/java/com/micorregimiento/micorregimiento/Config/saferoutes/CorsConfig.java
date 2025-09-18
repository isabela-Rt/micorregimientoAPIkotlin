package com.micorregimiento.micorregimiento.Config.saferoutes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // URLs y IPs permitidas - Configura según tus necesidades
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",          // Frontend local React/Angular
            "http://localhost:4200",          // Frontend local Angular
            "http://localhost:8080",          // Frontend local Vue/otros
            "https://tu-dominio.com",         // Dominio de producción
            "https://www.tu-dominio.com",     // Dominio de producción con www
            "http://192.168.1.100:3000",      // IP local específica
            "http://10.0.0.100:3000"          // Otra IP local específica
    );

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(ALLOWED_ORIGINS.toArray(new String[0]))
                .allowedMethods(ALLOWED_METHODS.toArray(new String[0]))
                .allowedHeaders(ALLOWED_HEADERS.toArray(new String[0]))
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight por 1 hora
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configurar orígenes permitidos
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);

        // Configurar métodos permitidos
        configuration.setAllowedMethods(ALLOWED_METHODS);

        // Configurar headers permitidos
        configuration.setAllowedHeaders(ALLOWED_HEADERS);

        // Permitir credenciales (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Headers que el cliente puede acceder
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // Tiempo de cache para preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
