package com.micorregimiento.micorregimiento.Generics.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SecurityContextService {

    public Long getCurrentUserId() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            Object userId = request.getAttribute("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserRoles() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            Object roles = request.getAttribute("userRoles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserPrivileges() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            Object privileges = request.getAttribute("userPrivileges");
            if (privileges instanceof List) {
                return (List<String>) privileges;
            }
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public List<Long> getCurrentUserNeighborhoods() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            Object neighborhoods = request.getAttribute("userNeighborhoods");
            if (neighborhoods instanceof List) {
                return (List<Long>) neighborhoods;
            }
        }
        return List.of();
    }

    public boolean hasRole(String roleName) {
        return getCurrentUserRoles().contains(roleName);
    }

    public boolean hasPrivilege(String privilegeName) {
        return getCurrentUserPrivileges().contains(privilegeName);
    }

    public boolean isAdmin() {
        return hasRole("admin");
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
