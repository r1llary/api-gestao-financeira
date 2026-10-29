package com.beca.usuarios.config;

import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import com.beca.usuarios.core.usuario.port.output.UserRepositoryPort;
import com.beca.usuarios.core.usuario.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort repository){
        return new UserService(repository);
    }
}
