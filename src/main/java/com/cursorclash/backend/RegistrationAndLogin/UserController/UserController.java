package com.cursorclash.backend.RegistrationAndLogin.UserController;



import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.LoginDTO;
import com.cursorclash.backend.RegistrationAndLogin.DTO.UserDTO;
import com.cursorclash.backend.RegistrationAndLogin.Service.UserService;
import com.cursorclash.backend.RegistrationAndLogin.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cursorclash.backend.RegistrationAndLogin.Service.DocumentService;
import com.cursorclash.backend.Document.PermissionType;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok().body("Server is up and running");
    }

    @PostMapping("/share-document")
    public void shareDocument(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String token) {
        Long documentId = Long.parseLong(requestBody.get("documentId").toString());
        String recipientEmail = requestBody.get("recipientEmail").toString();
        PermissionType permissionType = PermissionType.valueOf(requestBody.get("permissionType").toString());
        documentService.shareDocument(documentId, recipientEmail, permissionType, token);
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId, @RequestHeader("Authorization") String token) {
        try {
            documentService.deleteDocument(documentId, token);
            return ResponseEntity.ok("Document deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete document: " + e.getMessage());
        }
    }
    @PutMapping("/documents/{documentId}")
    public ResponseEntity<?> renameDocument(@PathVariable Long documentId,
                                            @RequestBody Map<String, String> requestBody,
                                            @RequestHeader("Authorization") String token) {
        String newName = requestBody.get("newName");
        try {
            Document renamedDocument = documentService.renameDocument(documentId, newName, token);
            if (renamedDocument != null) {
                return ResponseEntity.ok("Document renamed successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to rename document: " + e.getMessage());
        }
    }

    @PostMapping(path = "/documents")
    public ResponseEntity<?> createDocument(@RequestBody DocumentDTO documentDTO, @RequestHeader("Authorization") String token) {
        System.out.println("create document req" + documentDTO.toString());
        try {
            String documentId = documentService.createDocument(documentDTO, token);
            return ResponseEntity.ok("Document created successfully with ID: " + documentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create document: " + e.getMessage());
        }
    }

    @GetMapping("/documents/{documentId}")
    public ResponseEntity<?> openDocument(@PathVariable Long documentId, @RequestHeader("Authorization") String token) {
        try {
            String content = documentService.openDocument(documentId, token);
            if (content != null) {
                return ResponseEntity.ok(content);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to open document: " + e.getMessage());
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
