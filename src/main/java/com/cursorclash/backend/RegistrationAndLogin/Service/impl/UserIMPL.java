package com.cursorclash.backend.RegistrationAndLogin.Service.impl;

import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import com.cursorclash.backend.RegistrationAndLogin.Repo.UserRepo;
import com.cursorclash.backend.RegistrationAndLogin.Service.UserService;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserIMPL implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired

    private PasswordEncoder passwordEncoder;

    @Override
    public String addUser(UserDTO userDTO) {
        User user = new User(
                userDTO.getUserid(),
                userDTO.getUsername(),
                userDTO.getEmail(),
                this.passwordEncoder.encode(userDTO.getPassword())
        );
        userRepo.save(user);
        return user.getUsername();
    }

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        String msg = "";
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByEmail(loginDTO.getEmail()));
        if (optionalUser.isPresent()) {
            User newUser = optionalUser.get();
            String encodedPassword = newUser.getPassword();
            Boolean isPwdRight = passwordEncoder.matches(loginDTO.getPassword(), encodedPassword);
            if (isPwdRight) {
                return new LoginResponse("Login Success", true);
            } else {
                return new LoginResponse("Invalid Email or Password ", false);
            }
        } else {
            return new LoginResponse("Invalid Email or Password", false);
        }
    }
}
