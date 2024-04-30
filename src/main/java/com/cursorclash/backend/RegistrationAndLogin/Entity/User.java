package com.cursorclash.backend.RegistrationAndLogin.Entity;

import jakarta.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "user_id",length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userid;

    @Column(name = "username")
    @JsonProperty("user_name")
    @NotNull
    @NotBlank
    private String Username;

    @Column(name = "email")
    @NotNull
    @NotBlank
    private String email;

    @Column(name="password")
    @NotNull
    @NotBlank
    private String password;

    public User(){}

    public User(int userid, String username, String email, String password) {
        this.userid = userid;
        Username = username;
        this.email = email;
        this.password = password;
    }

    public int getUserid() {
        return userid;
    }

    public String getUsername() {
        return Username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", Username='" + Username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
