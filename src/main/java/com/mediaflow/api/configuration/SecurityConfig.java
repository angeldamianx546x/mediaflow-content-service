package com.mediaflow.api.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));
                configuration.setAllowCredentials(false);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // ===== GRAPHQL =====
                                                .requestMatchers("/graphql").permitAll()
                                                .requestMatchers("/graphiql").permitAll()
                                                .requestMatchers("/graphiql/**").permitAll()

                                                // Swagger/OpenAPI
                                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Categorías - Ver: todos autenticados, CRUD: solo ADMIN
                                                .requestMatchers(HttpMethod.GET, "/api/v1/categories/**")
                                                .hasAnyRole("VIEWER", "CREATOR", "ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/categories").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/categories/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**")
                                                .hasRole("ADMIN")

                                                // Contenidos - GET público para visualizar, POST solo CREATOR
                                                .requestMatchers(HttpMethod.GET, "/api/v1/contents/**")
                                                .hasAnyRole("VIEWER", "CREATOR", "ADMIN")
                                                // TEMPORARY: permitAll for testing
                                                .requestMatchers(HttpMethod.POST, "/api/v1/contents").permitAll()
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/contents/**")
                                                .hasAnyRole("CREATOR", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/contents/**")
                                                .hasAnyRole("CREATOR", "ADMIN")

                                                // Archivos antiguos - mantener compatibilidad
                                                .requestMatchers(HttpMethod.GET, "/api/v1/files/**")
                                                .hasAnyRole("VIEWER", "CREATOR", "ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/files/**")
                                                .hasAnyRole("CREATOR", "ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/files/**")
                                                .hasAnyRole("CREATOR", "ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/files/**")
                                                .hasAnyRole("CREATOR", "ADMIN")

                                                // Cualquier otra ruta requiere autenticación
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(jwtAuthFilter,
                                                org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class)
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint));

                return http.build();
        }

        @Bean
        public org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> tenantFilterRegistration(
                        JwtAuthenticationFilter filter) {
                org.springframework.boot.web.servlet.FilterRegistrationBean<JwtAuthenticationFilter> registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(
                                filter);
                registration.setEnabled(false);
                return registration;
        }
}