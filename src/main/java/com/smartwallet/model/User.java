package com.smartwallet.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
name = "users",
indexes = {
@Index(
name = "idx_user_email",
columnList = "email"
)
}
)
public class User {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int userId;

private String name;

@Column(unique = true, nullable = false)
private String email;

private String role;

@JsonIgnore
private String password;

// Default Constructor
public User() {
}

// Parameterized Constructor
public User(String name, String email, String password, String role) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
}

// Getters and Setters

public int getUserId() {
    return userId;
}

public void setUserId(int userId) {
    this.userId = userId;
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

public String getRole() {
    return role;
}

public void setRole(String role) {
    this.role = role;
}

}
