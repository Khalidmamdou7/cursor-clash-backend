package com.cursorclash.backend.Authentication.services.impl;

import com.cursorclash.backend.Authentication.DTOs.*;
import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Authentication.repositories.UserRepo;
import com.cursorclash.backend.Authentication.services.UserService;
import com.cursorclash.backend.exceptions.CustomExceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cursorclash.backend.Authentication.utils.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

@Service
public class UserIMPL implements UserService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;




    
    @Override
    public UserResponseDTO addUser(RegReqDTO userRegDTO) {
        User existingUser = userRepo.findByEmail(userRegDTO.getEmail());
        if (existingUser != null) {
            throw new BadRequestException("User with email " + userRegDTO.getEmail() + " already exists.");
        }

        User user = new User(
                userRegDTO.getUsername(),
                userRegDTO.getEmail(),
                this.passwordEncoder.encode(userRegDTO.getPassword())
        );
        userRepo.save(user);
        UserResponseDTO userResponseDTO = new UserResponseDTO(
                user.getUserid(),
                user.getUsername(),
                user.getEmail()
        );
        return userResponseDTO;
    }

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        String msg = "";
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByEmail(loginDTO.getEmail()));
        if (optionalUser.isPresent()) {
            User newUser = optionalUser.get();
            String encodedPassword = newUser.getPassword();
            Boolean isPwdRight = this.passwordEncoder.matches(loginDTO.getPassword(), encodedPassword);
            if (isPwdRight) {
                String token = jwtTokenProvider.generateToken(loginDTO.getEmail());
                return new LoginResponse("Login Success", true, token);

            } else {
                return new LoginResponse("Invalid Email or Password ", false);
            }
        } else {
            return new LoginResponse("Invalid Email or Password", false);
        }
    }
}
