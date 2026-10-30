package com.beca.usuarios.adapter.output.persistence;

import com.beca.usuarios.core.usuario.model.User;
import com.beca.usuarios.core.usuario.port.output.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaRepository;

    public UserPersistenceAdapter(JpaUserRepository jpaRepository){
        this.jpaRepository=jpaRepository;
    }

    @Override
    public User save(User user){
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash()
        );
        UserEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<User> findAll(){
        List<UserEntity> entities = jpaRepository.findAll();
         List<User> users = new ArrayList<>();

         for (UserEntity entity: entities){
             User user = toDomain(entity);
             users.add(user);
         }
         return users;
    }

    @Override
    public Optional<User> findById(Long id){
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email){
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email){
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(Long id){
        jpaRepository.deleteById(id);
    }


    private User toDomain(UserEntity entity){
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash()
        );
    }
}
