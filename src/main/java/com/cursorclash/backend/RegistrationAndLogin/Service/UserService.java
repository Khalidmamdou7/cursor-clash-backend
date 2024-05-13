package com.cursorclash.backend.RegistrationAndLogin.Service;


import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserResponseDTO;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;

public interface UserService {
    UserResponseDTO addUser(UserDTO userDTO);
    LoginResponse login(LoginDTO loginDTO);

}
