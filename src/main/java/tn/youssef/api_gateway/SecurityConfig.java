package tn.youssef.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveJwtDecoder jwtDecoder;

    public SecurityConfig(ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        // Allow all OPTIONS requests (CORS preflight)
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public endpoints (no authentication required)
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/users/**").permitAll()
                        .pathMatchers("/api/v1/notifications/ws/**").permitAll()
                        .pathMatchers("/api/v1/notifications/admin/send").permitAll()
                        .pathMatchers("/uploads/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/fallback/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/forum/**").permitAll()
                        .pathMatchers("/api/forum/**").authenticated()
                        // Block internal endpoints (defense in depth)
                        .pathMatchers("/internal/**").denyAll()
                        .pathMatchers("/api/v1/settings/**").permitAll()
                        // Everything else requires JWT authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))  // ← wired
                )
                .build();
    }
}