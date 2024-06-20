package com.cursorclash.backend.Authentication.services;


import com.cursorclash.backend.Authentication.DTOs.*;

public interface UserService {
    UserResponseDTO addUser(RegReqDTO userRegDTO);
    LoginResponse login(LoginDTO loginDTO);

}
