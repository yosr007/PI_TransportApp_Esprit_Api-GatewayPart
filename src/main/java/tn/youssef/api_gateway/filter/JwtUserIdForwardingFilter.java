package tn.youssef.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import java.util.List;

@Component
public class JwtUserIdForwardingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.getPrincipal() instanceof Jwt)
                .map(auth -> (Jwt) auth.getPrincipal())
                .flatMap(jwt -> {
                    // Extract the subject (user ID) from the JWT claims
                    String userId = jwt.getSubject();

                    // Extract roles from JWT claims
                    Object rolesClaim = jwt.getClaim("roles");
                    String rolesHeader = extractRolesHeader(rolesClaim);

                    if (userId != null) {
                        // Mutate the request to add custom headers
                        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                                .header("X-User-Id", userId);

                        if (rolesHeader != null) {
                            requestBuilder.header("X-User-Roles", rolesHeader);
                        }

                        ServerHttpRequest mutatedRequest = requestBuilder.build();

                        // Forward the mutated request downstream
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }
                    return chain.filter(exchange);
                })
                // If no JWT found (e.g. public routes), just proceed normally
                .switchIfEmpty(chain.filter(exchange));
    }

    /**
     * Extract roles from JWT claim into comma-separated string for header
     * JWT may contain roles as String[] or single String
     */
    private String extractRolesHeader(Object rolesClaim) {
        if (rolesClaim == null) {
            return null;
        }
        try {
            if (rolesClaim instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) rolesClaim;
                return String.join(",", roles);
            } else if (rolesClaim instanceof String) {
                return (String) rolesClaim;
            }
        } catch (Exception e) {
            // Log warning but don't fail
            System.err.println("Failed to parse roles claim: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int getOrder() {
        // Runs after standard security filters but before routing
        return 0;
    }
}
