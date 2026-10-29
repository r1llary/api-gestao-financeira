package com.beca.usuarios.core.usuario.port.output;

import com.beca.usuarios.core.usuario.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    void deleteById(Long id);
}
