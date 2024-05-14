package com.cursorclash.backend.RegistrationAndLogin.Service;
import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.Document.DocumentPermission;
import com.cursorclash.backend.Document.PermissionType;
import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import com.cursorclash.backend.RegistrationAndLogin.JWT.JwtTokenProvider;
import com.cursorclash.backend.RegistrationAndLogin.Repo.DocumentPermissionRepo;
import com.cursorclash.backend.RegistrationAndLogin.Repo.DocumentRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cursorclash.backend.RegistrationAndLogin.Repo.UserRepo;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DocumentPermissionRepo documentPermissionRepo;

    public void shareDocument(Long documentId, String recipientEmail, PermissionType permissionType, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);

        try {
            // Find the document by ID
            Optional<Document> optionalDocument = documentRepo.findById(documentId);
            if (!optionalDocument.isPresent()) {
                throw new RuntimeException("Document with ID " + documentId + " not found.");
            }

            Document document = optionalDocument.get();

            // Check if the current user is the owner of the document
            if (!document.getOwner().equals(currentUser)) {
                throw new RuntimeException("You don't have permission to share this document.");
            }

            // Find the user by email
            User recipientUser = userRepo.findByEmail(recipientEmail);
            if (recipientUser == null) {
                throw new RuntimeException("Recipient with email " + recipientEmail + " not found.");
            }

            // Check if the same permission already exists
            List<DocumentPermission> existingPermissions = documentPermissionRepo.findByDocumentAndGranteeAndPermissionType(document, recipientUser, permissionType);
            if (!existingPermissions.isEmpty()) {
                throw new RuntimeException("This document is already shared with the recipient with the same permission.");
            }

            DocumentPermission documentPermission = new DocumentPermission();
            documentPermission.setDocument(document);
            documentPermission.setGrantee(recipientUser);
            documentPermission.setPermissionType(permissionType);

            documentPermissionRepo.save(documentPermission);

        } catch (Exception e) {
            throw new RuntimeException("Error sharing document: " + e.getMessage(), e);
        }
    }

    public boolean hasPermission(Long documentId, int userId, PermissionType permissionType) {
        // Retrieve the document
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();

            // Retrieve the user
            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Checking permission for the document
                List<DocumentPermission> permissions = documentPermissionRepo.findByDocumentAndGranteeAndPermissionType(document, user, permissionType);
                return !permissions.isEmpty();
            } else {
                // Handle case where user is not found
                throw new RuntimeException("User with ID " + userId + " not found.");
            }
        } else {
            // Handle case where document is not found
            throw new RuntimeException("Document with ID " + documentId + " not found.");
        }
    }



    public Long createDocument(DocumentDTO documentDTO, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);

        // Create a new Document entity
        Document document = new Document();
        document.setName(documentDTO.getName());
        document.setContent(documentDTO.getContent());
        document.setOwner(currentUser);

        Document savedDocument = documentRepo.save(document);

        for (PermissionType permissionType : PermissionType.values()) {
            DocumentPermission documentPermission = new DocumentPermission();
            documentPermission.setDocument(savedDocument);
            documentPermission.setGrantee(currentUser);
            documentPermission.setPermissionType(permissionType);
            documentPermissionRepo.save(documentPermission);
        }

        return savedDocument.getId();
    }



    public List<Document> listDocuments(User user) {
        return documentRepo.findByOwner(user);
    }


    // you can rename it if u got permission to write or owner
    public Document renameDocument(Long documentId, String newName, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        int userid = currentUser.getUserid();
        if (hasPermission(documentId, userid,PermissionType.WRITE)) {
            Optional<Document> optionalDocument = documentRepo.findById(documentId);
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                document.setName(newName);
                return documentRepo.save(document);
            }
        } else {
            throw new RuntimeException("You dont have permission to rename it");
        }
        return null;
    }


    @Transactional
    public void deleteDocument(Long documentId, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        int userId = currentUser.getUserid();

        if (hasPermission(documentId, userId, PermissionType.DELETE)) {
            Optional<Document> optionalDocument = documentRepo.findById(documentId);

            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();

                // Owner checking
                if (document.getOwner().equals(currentUser)) {
                    documentPermissionRepo.deleteByDocument(document);
                    documentRepo.delete(document);
                } else {
                    throw new RuntimeException("You are not the owner of the document.");
                }
            } else {
                throw new RuntimeException("Document not found.");
            }
        } else {
            throw new RuntimeException("You don't have permission to delete the document.");
        }
    }

    public DocumentDTO openDocument(Long documentId, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        int userid = currentUser.getUserid();
        if (hasPermission(documentId, userid,PermissionType.READ)) {
            Optional<Document> optionalDocument = documentRepo.findById(documentId);
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                DocumentDTO documentDTO = new DocumentDTO(
                        document.getId(),
                        document.getName(),
                        document.getOwner().getUserid(),
                        document.getContent());
                return documentDTO;
            }
        }
        throw new RuntimeException("You dont have permission to open it");
    }

    public List<DocumentDTO> getOwnedDocuments(String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        List<DocumentDTO> ownedDocuments = new ArrayList<>();
        List<Document> documents = documentRepo.findByOwner(currentUser);

        for (Document document : documents) {
            ownedDocuments.add(new DocumentDTO(
                    document.getId(),
                    document.getName(),
                    document.getOwner().getUserid(),
                    document.getContent()
            ));
        }

        return ownedDocuments;
    }

    public List<DocumentDTO> getSharedDocuments(String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        List<DocumentDTO> sharedDocuments = new ArrayList<>();
        List<DocumentPermission> documentPermissions = documentPermissionRepo.findByGrantee(currentUser);
        Set<Long> addedDocumentIds = new HashSet<>();

        for (DocumentPermission documentPermission : documentPermissions) {
            Document document = documentPermission.getDocument();
            Long documentId = document.getId();

            if (!addedDocumentIds.contains(documentId)) {

                if (documentPermission.getPermissionType() != PermissionType.DELETE) {
                    sharedDocuments.add(new DocumentDTO(
                            documentId,
                            document.getName(),
                            document.getOwner().getUserid(),
                            document.getContent()
                    ));
                }
                addedDocumentIds.add(documentId);
            } else {
                if (documentPermission.getPermissionType() == PermissionType.DELETE) {
                    sharedDocuments.removeIf(doc -> doc.getId().equals(documentId));
                }
            }
        }

        return sharedDocuments;
    }



}
