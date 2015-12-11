package com.framgia.elsytem.model;

/**
 * Created by avishek on 12/14/15.
 */
public class User {
    String name, email, password, password_confirmation;

    public void User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }
}
