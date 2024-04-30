package com.cursorclash.backend.RegistrationAndLogin.UserController;



import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.Service.UserService;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "api/user")


public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public String saveUser(@RequestBody UserDTO userDTO){
        String id = userService.addUser(userDTO);
        return id;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        LoginResponse loginMessage = userService.login(loginDTO);
        return ResponseEntity.ok(loginMessage);
    }

}
