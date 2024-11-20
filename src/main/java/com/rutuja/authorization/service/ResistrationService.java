package com.rutuja.authorization.service;

import com.rutuja.authorization.bean.ResistrationBean;
import com.rutuja.authorization.exceptions.AuthorizationException;
import com.rutuja.authorization.repo.ResistrationRepository;
import jakarta.annotation.security.DenyAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ResistrationService {
private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResistrationService.class);
@Autowired
private R2dbcEntityTemplate r2dbcEntityTemplate;
    @Autowired
    private ResistrationRepository resistrationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<String> userRegistration(ResistrationBean registrationBean) {
        return resistrationRepository.findById(registrationBean.getEmail())
                .flatMap(existingUser -> Mono.just("Already exists"))
                .switchIfEmpty(Mono.defer(() -> {
                    registrationBean.setPassword(passwordEncoder.encode(registrationBean.getPassword()));
                    return r2dbcEntityTemplate.insert(registrationBean)
                            .switchIfEmpty(Mono.error(new Exception("not saved")))
                            .flatMap(user -> Mono.just("Success"));
                }))
                .onErrorResume(error -> {
                    logger.error("Error during user registration: ", error);
                    return Mono.just("error");
                });
    }
}

