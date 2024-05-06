package com.cursorclash.backend.RegistrationAndLogin.Service.impl;

import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import com.cursorclash.backend.RegistrationAndLogin.Repo.DocumentRepo;
import com.cursorclash.backend.RegistrationAndLogin.Repo.UserRepo;
import com.cursorclash.backend.RegistrationAndLogin.Service.UserService;
import com.cursorclash.backend.RegistrationAndLogin.config.AuthenticationFacade;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cursorclash.backend.RegistrationAndLogin.JWT.JwtTokenProvider;
import java.util.Optional;
import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;

@Service
public class UserIMPL implements UserService {

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepo userRepo;

    @Autowired

    private PasswordEncoder passwordEncoder;


    @Override
    public String createDocument(DocumentDTO documentDTO) {
        // Retrieve current authenticated user
        User currentUser = authenticationFacade.getCurrentUser();

        // Create a new Document entity
        Document document = new Document();
        document.setName(documentDTO.getName());
        document.setContent(documentDTO.getContent());
        document.setOwner(currentUser);

        // Save the document to the database
        Document savedDocument = documentRepo.save(document);

        return String.valueOf(savedDocument.getId());
    }
    
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
