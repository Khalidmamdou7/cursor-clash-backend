package com.cursorclash.backend.RegistrationAndLogin.UserController;



import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.Service.UserService;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "api/user")


public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/documents")
    public ResponseEntity<?> createDocument(@RequestBody DocumentDTO documentDTO, @RequestHeader("Authorization") String token) {
        System.out.println("create document req" + documentDTO.toString());
        try {
            String documentId = userService.createDocument(documentDTO, token);
            return ResponseEntity.ok("Document created successfully with ID: " + documentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create document: " + e.getMessage());
        }
    }

    @PostMapping(path = "/register")
    public String saveUser(@RequestBody UserDTO userDTO){
        System.out.println("register req" + userDTO.toString());
        String id = userService.addUser(userDTO);
        return id;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        System.out.println("login req" + loginDTO.toString());
        LoginResponse loginMessage = userService.login(loginDTO);
        if (loginMessage.getStatus()) {
            return ResponseEntity.ok(loginMessage);
        } else {
            return ResponseEntity.badRequest().body(loginMessage);
        }
    }

}
