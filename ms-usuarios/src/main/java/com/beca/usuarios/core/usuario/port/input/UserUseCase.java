package com.beca.usuarios.core.usuario.port.input;

import com.beca.usuarios.core.usuario.model.User;

import java.util.List;

// Define quais funcionalidades o sistema oferece
public interface UserUseCase {

    User create(User user);

    List<User> findAll();

    User findById(Long id);

    User update(Long id, User user);

    void deleteById(Long id);
}
