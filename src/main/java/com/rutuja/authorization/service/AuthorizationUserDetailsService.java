package com.rutuja.authorization.service;

import com.rutuja.authorization.entity.UserEntity;
import com.rutuja.authorization.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@Service
public class AuthorizationUserDetailsService implements ReactiveUserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        System.out.println("Service called");
        return userRepository.findById(username).map(UserEntity::new);
    }
}
