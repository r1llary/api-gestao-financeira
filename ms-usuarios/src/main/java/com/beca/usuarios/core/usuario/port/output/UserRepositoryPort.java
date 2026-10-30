package com.beca.usuarios.core.usuario.port.output;

import com.beca.usuarios.core.usuario.model.User;

import java.util.List;
import java.util.Optional;

//Porta de saída, define quais operações o UserService precisa realizar
public interface UserRepositoryPort {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}
