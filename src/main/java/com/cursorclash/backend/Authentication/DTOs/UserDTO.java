package com.cursorclash.backend.Authentication.DTOs;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {

    private int userid;
    private String username;
    private String email;
    private String password;

}
