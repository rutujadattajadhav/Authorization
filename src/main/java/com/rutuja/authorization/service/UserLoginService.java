package com.rutuja.authorization.service;

import com.rutuja.authorization.bean.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserLoginService {

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;
    @Autowired
    private JWTService jwtService;
    public Mono<String> verify(UserBean user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Mono<Authentication> authenticationMono = reactiveAuthenticationManager.authenticate(authentication);

        return authenticationMono.flatMap(auth -> {
            if (auth.isAuthenticated()) {
                return Mono.just(jwtService.generateToken(user.getUsername()));
            } else {
                return Mono.just("fail");
            }
        }).onErrorReturn("fail");
    }
}
