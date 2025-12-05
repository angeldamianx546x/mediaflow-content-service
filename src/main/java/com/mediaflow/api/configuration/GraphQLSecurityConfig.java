package com.mediaflow.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mediaflow.api.service.JwtService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class GraphQLSecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Bean
    public WebGraphQlInterceptor authInterceptor() {
        return new WebGraphQlInterceptor() {
            @Override
            public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
                String authHeader = request.getHeaders().getFirst("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String jwt = authHeader.substring(7);

                    try {
                        String userEmail = jwtService.extractUsername(jwt);

                        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                            if (jwtService.isTokenValid(jwt)) {
                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        }
                    } catch (Exception e) {
                        // Log error but continue
                        System.err.println("Error processing JWT in GraphQL: " + e.getMessage());
                    }
                }

                return chain.next(request);
            }
        };
    }
}