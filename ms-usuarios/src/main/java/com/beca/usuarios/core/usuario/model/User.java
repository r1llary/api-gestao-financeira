package com.beca.usuarios.core.usuario.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;

    public User(Long id, String name, String email, String passwordHash) {

        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("O nome é obrigatório!");
        }
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("O e-mail é obrigatório!");
        }
        if (passwordHash == null || passwordHash.isBlank()){
            throw new IllegalArgumentException("A credecial do usuário é obrigatória!");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(){

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
