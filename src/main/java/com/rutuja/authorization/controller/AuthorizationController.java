package com.rutuja.authorization.controller;

import com.rutuja.authorization.bean.ResistrationBean;
import com.rutuja.authorization.bean.UserBean;
import com.rutuja.authorization.repo.UserRepository;
import com.rutuja.authorization.service.ResistrationService;
import com.rutuja.authorization.service.UserLoginService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController

public class AuthorizationController {
    @Autowired
    private  UserLoginService service;
    @Autowired
    private UserRepository statePrepojiotory;

    @Autowired
    private ResistrationService resistrationService;
    @RequestMapping(value = "/test")
    public String  test(){
        return  "test succss";
    }

    @RequestMapping(value = "/auth")
    public String  auth(){
        return  "auth succss";
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody UserBean user) {
        return service.verify(user);
    }

    @PostMapping("/saveUser")
    public Mono<String> saveUser(  @RequestBody ResistrationBean resistrationBean) {
        return resistrationService.userRegistration(resistrationBean);
    }
}
