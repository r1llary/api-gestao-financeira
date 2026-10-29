package com.beca.usuarios.core.usuario.port.input;

import com.beca.usuarios.core.usuario.model.User;

import java.util.List;

public interface UserUseCase {

    User create(User user);

    List<User> findAll();

    User findById(Long id);

    User update(Long id, User user);

    void deleteById(Long id);
}
