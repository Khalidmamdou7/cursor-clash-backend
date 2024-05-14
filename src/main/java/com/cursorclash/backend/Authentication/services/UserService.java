package com.cursorclash.backend.Authentication.services;


import com.cursorclash.backend.Authentication.DTOs.LoginDTO;
import com.cursorclash.backend.Authentication.DTOs.UserDTO;
import com.cursorclash.backend.Authentication.DTOs.UserResponseDTO;
import com.cursorclash.backend.Authentication.DTOs.LoginResponse;

public interface UserService {
    UserResponseDTO addUser(UserDTO userDTO);
    LoginResponse login(LoginDTO loginDTO);

}
