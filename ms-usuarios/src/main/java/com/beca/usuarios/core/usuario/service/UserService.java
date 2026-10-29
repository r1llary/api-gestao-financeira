package com.beca.usuarios.core.usuario.service;

import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.input.UserUseCase;
import com.beca.usuarios.core.usuario.port.output.UserRepositoryPort;

import java.util.List;
import java.util.NoSuchElementException;

public class UserService implements UserUseCase {

    private final UserRepositoryPort respository;

    public UserService(UserRepositoryPort respository) {
        this.respository = respository;
    }

    @Override
    public User create(User user){
        if (respository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        return respository.save(user);
    }

    @Override
    public List<User> findAll(){
        return respository.findAll();
    }

    @Override
    public User findById(Long id){
        return respository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found" + id));
    }

    @Override
    public User update(Long id, User user){
        User existingUser = findById(id);

        if (!existingUser.getEmail().equalsIgnoreCase(user.getEmail())
            && respository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("Email already registered");
        }

        User updatedUser = new User(
                id,
                user.getName(),
                user.getEmail(),
                user.getPasswordHash()
        );

        return respository.save(updatedUser);
    }

    @Override
    public void deleteById(Long id){
        findById(id);

        respository.deleteById(id);
    }
}
