package com.beca.transacoes.adapter.input.rest;


import com.beca.transacoes.adapter.input.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @GetMapping("/me")
    public AuthenticatedUser findMe(
        @AuthenticationPrincipal AuthenticatedUser user

    ){
        return user;
    }

}
