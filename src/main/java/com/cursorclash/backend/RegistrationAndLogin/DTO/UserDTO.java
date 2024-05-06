package com.cursorclash.backend.RegistrationAndLogin.DTO;

import com.cursorclash.backend.RegistrationAndLogin.Entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class UserDTO {

    private int userid;
    private String Username;
    private String email;
    private String password;

    private UserRole role;

    public UserDTO(int userid, String username, String email, String password, UserRole role) {
        this.userid = userid;
        Username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserDTO(){}

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
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
        return "UserDTO{" +
                "userid=" + userid +
                ", Username='" + Username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
