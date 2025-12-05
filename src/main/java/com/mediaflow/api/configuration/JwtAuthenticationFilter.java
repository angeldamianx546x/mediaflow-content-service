package com.mediaflow.api.configuration;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mediaflow.api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JwtAuthenticationFilter: Entering filter for " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si no hay header de autorización o no empieza con "Bearer ", continuar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtAuthenticationFilter: No token found or invalid header: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token
        jwt = authHeader.substring(7);
        System.out.println("JwtAuthenticationFilter: Token extracted for " + request.getRequestURI());

        try {
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("JwtAuthenticationFilter: Username extracted: " + userEmail);

            // Si hay un email y el usuario no está autenticado aún (o es anónimo)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (userEmail != null && (auth == null || auth instanceof AnonymousAuthenticationToken)) {
                System.out.println("JwtAuthenticationFilter: Validating token...");

                // Validar token (solo expiración y firma)
                if (jwtService.isTokenValid(jwt)) {
                    System.out.println("JwtAuthenticationFilter: Token is valid");
                    // Extraer roles y userId del token
                    List<String> roles = jwtService.extractRoles(jwt);
                    System.out.println("JwtAuthenticationFilter: Raw roles: " + roles);
                    Integer userId = jwtService.extractUserId(jwt);
                    System.out.println("JwtAuthenticationFilter: UserId: " + userId);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                            .collect(Collectors.toList());

                    System.out.println("JwtAuthenticationFilter: Authorities: " + authorities);

                    UserPrincipal principal = new UserPrincipal(userId, userEmail);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JwtAuthenticationFilter: Authentication set for " + userEmail);
                } else {
                    System.out.println("JwtAuthenticationFilter: Token invalid according to jwtService");
                }
            } else {
                 System.out.println("JwtAuthenticationFilter: User already authenticated or email null");
            }
        } catch (Exception e) {
            // Log del error (opcional)
            System.err.println("Error al procesar el token JWT: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    @Data
    @AllArgsConstructor
    public static class UserPrincipal {
        private Integer userId;
        private String email;
    }
}
