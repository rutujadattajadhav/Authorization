package com.rutuja.authorization.filter;
import com.rutuja.authorization.service.AuthorizationUserDetailsService;
import com.rutuja.authorization.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final JWTService jwtService;
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    @Autowired
    private AuthorizationUserDetailsService authorizationUserDetailsService;
    public JwtAuthenticationFilter(JWTService jwtService, ReactiveUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.reactiveUserDetailsService = userDetailsService;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Get the Authorization header from the HTTP request
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // 2. If no Authorization header or the header does not start with "Bearer ", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        // 3. Extract the JWT token by removing the "Bearer " prefix
        String authToken = authHeader.substring(7);
        String username;
        try {
            // 4. Extract the username from the token using the JWT service
            username = jwtService.extractUserName(authToken);
        } catch (Exception e) {
            // 5. If token extraction fails, set response status to Unauthorized and complete the response
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 6. Find the user details by username using the authorization service
        return authorizationUserDetailsService.findByUsername(username)
                .flatMap(userDetails -> {
                    // 7. Validate the token against the user details
                    return jwtService.validateToken(authToken, userDetails)
                            .flatMap(isValid -> {
                                if (Boolean.TRUE.equals(isValid)) {
                                    // 8. If token is valid, set the authentication context
                                    return updateSecurityContextAndFilterChain(exchange,chain,userDetails);
                                } else {
                                    // 11. If the token is invalid, set response status to Unauthorized and complete response
                                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                    return exchange.getResponse().setComplete();
                                }
                            }).onErrorResume(e -> {
                                // 12. Handle errors during token validation
                                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                return exchange.getResponse().setComplete();
                            });
                })
                // 13. If user details are not found, throw an error
                .switchIfEmpty(Mono.defer(() -> {
                    // Log an error or do additional actions if user details are not found
                    System.out.println("User not found: " + username);
                    // Optionally, you can set the response status to Unauthorized
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    // Complete the response
                    return exchange.getResponse().setComplete();
                }))
                .onErrorResume(e -> {
                    // 14. Handle errors for the entire filter process
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }
    public Mono<Void> updateSecurityContextAndFilterChain(ServerWebExchange exchange, WebFilterChain chain, UserDetails userDetails) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> {
                    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()));
                })
                .flatMap(securityContext -> chain.filter(exchange))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                ));
    }
}