package com.mediaflow.api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mediaflow.api.configuration.JwtAuthenticationFilter.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    /**
     * Obtiene el email del usuario autenticado actualmente
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getEmail();
        }
        return authentication.getName();
    }

    /**
     * Obtiene el ID del usuario autenticado
     */
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUserId();
        }
        throw new IllegalStateException("No se pudo obtener el ID del usuario del token");
    }

    /**
     * Verifica si el usuario autenticado es el propietario del recurso
     */
    public boolean isOwner(Integer userId) {
        return getCurrentUserId().equals(userId);
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;

        return authentication.getAuthorities().stream()
                .anyMatch(
                        role -> role.getAuthority().equals(roleName) || role.getAuthority().equals("ROLE_" + roleName));
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("ROLE_ADMIN");
    }

    /**
     * Verifica si el usuario puede acceder al recurso
     * (es el propietario o es admin)
     */
    public boolean canAccess(Integer userId) {
        return isOwner(userId) || isAdmin();
    }

    /**
     * Lanza excepción si el usuario no puede acceder al recurso
     */
    public void validateAccess(Integer userId) {
        if (!canAccess(userId)) {
            throw new SecurityException("No tienes permiso para acceder a este recurso");
        }
    }
}