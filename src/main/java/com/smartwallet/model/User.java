package com.smartwallet.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            unique = true,
            nullable = false,
            length = 255
    )
    private String email;

    @Column(
            nullable = false,
            length = 30
    )
    private String role;

    @JsonIgnore
    @Column(
            nullable = false,
            length = 255
    )
    private String password;
private static final long serialVersionUID = 1L;
    // ============================================================
    // DEFAULT CONSTRUCTOR
    // ============================================================

    public User() {
    }

    // ============================================================
    // PARAMETERIZED CONSTRUCTOR
    // ============================================================

    public User(
            String name,
            String email,
            String password,
            String role) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

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