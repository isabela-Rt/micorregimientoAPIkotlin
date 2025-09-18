package com.micorregimiento.micorregimiento.Config.saferoutes;

import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    public JwtAuthenticationFilter(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            TokenValidationResponse validation = jwtService.validateToken(token);

            if (validation.isValid() && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Crear authorities basados en roles y privilegios
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                // Agregar roles con prefijo ROLE_
                if (validation.getRoles() != null) {
                    authorities.addAll(validation.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList()));
                }

                // Agregar privilegios
                if (validation.getPrivilegios() != null) {
                    authorities.addAll(validation.getPrivilegios().stream()
                            .map(privilege -> new SimpleGrantedAuthority(privilege))
                            .collect(Collectors.toList()));
                }

                // Crear token de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        validation.getEmail(),
                        null,
                        authorities
                );

                // Agregar detalles del usuario al token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Agregar información adicional al contexto
                request.setAttribute("userId", validation.getUserId());
                request.setAttribute("userRoles", validation.getRoles());
                request.setAttribute("userPrivileges", validation.getPrivilegios());
                request.setAttribute("userNeighborhoods", validation.getBarrioIds());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Error al procesar el token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
