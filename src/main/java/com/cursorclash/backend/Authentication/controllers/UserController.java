package com.cursorclash.backend.Authentication.controllers;



import com.cursorclash.backend.Authentication.DTOs.*;
import com.cursorclash.backend.Authentication.services.UserService;
import com.cursorclash.backend.Authentication.DTOs.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/auth")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping(path = "/register")
    public ResponseEntity<UserResponseDTO> saveUser(@RequestBody RegReqDTO userDTO){
        System.out.println("register req");
        UserResponseDTO user = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO loginDTO){
        System.out.println("login req" + loginDTO.toString());
        LoginResponse loginMessage = userService.login(loginDTO);
        if (loginMessage.getStatus()) {
            return ResponseEntity.ok(loginMessage);
        } else {
            return ResponseEntity.badRequest().body(loginMessage);
        }
    }





}
