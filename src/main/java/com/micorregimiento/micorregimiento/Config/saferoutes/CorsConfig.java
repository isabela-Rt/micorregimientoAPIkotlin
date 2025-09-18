package com.micorregimiento.micorregimiento.Config.saferoutes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // URLs permitidas - REEMPLAZA CON TUS URLs REALES
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",          // React local
            "http://localhost:4200",          // Angular local
            "http://localhost:8080",          // Otro frontend local
            "http://127.0.0.1:3000",          // Localhost alternativo
            "https://tu-dominio.com",         // Producción (reemplazar)
            "https://www.tu-dominio.com"      // Producción con www (reemplazar)
    );

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // IMPORTANTE: Usar allowedOrigins específicos, NO "*"
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition",
                "Content-Length"
        ));

        // PERMITIR CREDENCIALES (JWT, cookies, etc.)
        configuration.setAllowCredentials(true);

        // Cache preflight requests por 1 hora
        configuration.setMaxAge(3600L);

        // Aplicar configuración a todas las rutas /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}

