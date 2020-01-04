package com.baeldung.um.web.model;

public class User {
    private long id;
    private String email;
    private String password;

    public User() {
        super();
    }

    public User(long id, String email, String password) {
        super();
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", password=" + password + "]";
    }

}