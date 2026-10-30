package com.beca.usuarios.adapter.input.security;

import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.output.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceAdapter implements UserDetailsService {

    private final UserRepositoryPort repository;

    public UserDetailsServiceAdapter(UserRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        Optional<User> result = repository.findByEmail(email);

        if(result.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }
        User user = result.get();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
    }
}
