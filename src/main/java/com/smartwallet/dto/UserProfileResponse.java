
package com.smartwallet.dto;

public class UserProfileResponse {

    private int userId;
    private String name;
    private String email;
    private String role;

    public UserProfileResponse() {
    }

    public UserProfileResponse(
            int userId,
            String name,
            String email,
            String role
    ) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
