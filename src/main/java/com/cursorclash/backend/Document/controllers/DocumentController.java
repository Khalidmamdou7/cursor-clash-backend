package com.cursorclash.backend.Document.controllers;

import com.cursorclash.backend.Document.DTOs.DocumentCreationResponseDTO;
import com.cursorclash.backend.Document.DTOs.DocumentDTO;
import com.cursorclash.backend.Authentication.services.DocumentService;
import com.cursorclash.backend.Document.entities.Document;
import com.cursorclash.backend.Document.DTOs.PermissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(path = "/")
    public ResponseEntity<?> createDocument(@RequestBody DocumentDTO documentDTO, @RequestHeader("Authorization") String token) {
        System.out.println("create document req with name: " + documentDTO.getName());
        try {
            Long documentId = documentService.createDocument(documentDTO, token);
            DocumentCreationResponseDTO response = new DocumentCreationResponseDTO(
                    documentId,
                    documentDTO.getName(),
                    documentDTO.getOwnerId(),
                    documentDTO.getContent()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create document: " + e.getMessage());
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> openDocument(@PathVariable Long documentId, @RequestHeader("Authorization") String token) {
        try {
            DocumentDTO document = documentService.openDocument(documentId, token);
            return ResponseEntity.status(HttpStatus.OK).body(document);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to open document: " + e.getMessage());
        }
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<?> renameDocument(@PathVariable Long documentId,
                                            @RequestBody Map<String, String> requestBody,
                                            @RequestHeader("Authorization") String token) {
        String newName = requestBody.get("newName");
        try {
            Document renamedDocument = documentService.renameDocument(documentId, newName, token);
            if (renamedDocument != null) {
                DocumentDTO renamedDocDTO = new DocumentDTO(
                        renamedDocument.getId(),
                        renamedDocument.getName(),
                        renamedDocument.getOwner().getUserid(),
                        renamedDocument.getContent()
                );
                return ResponseEntity.status(HttpStatus.OK).body(renamedDocDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to rename document: " + e.getMessage());
        }
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId, @RequestHeader("Authorization") String token) {
        try {
            documentService.deleteDocument(documentId, token);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete document: " + e.getMessage());
        }
    }

    @PostMapping("/share-document")
    public ResponseEntity<?> shareDocument(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String token) {
        Long documentId = Long.parseLong(requestBody.get("documentId").toString());
        String recipientEmail = requestBody.get("recipientEmail").toString();
        PermissionType permissionType = PermissionType.valueOf(requestBody.get("permissionType").toString());
        documentService.shareDocument(documentId, recipientEmail, permissionType, token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/owned")
    public ResponseEntity<?> getOwnedDocuments(@RequestHeader("Authorization") String token) {
        try {
            List<DocumentDTO> ownedDocuments = documentService.getOwnedDocuments(token);
            return ResponseEntity.ok(ownedDocuments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve owned documents: " + e.getMessage());
        }
    }

    @GetMapping("/shared")
    public ResponseEntity<?> getSharedDocuments(@RequestHeader("Authorization") String token) {
        try {
            List<DocumentDTO> sharedDocuments = documentService.getSharedDocuments(token);
            return ResponseEntity.ok(sharedDocuments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve owned documents: " + e.getMessage());
        }
    }

}
