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
import java.util.List;
import java.util.Optional;

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

            // Find the recipient user by email
            User recipientUser = userRepo.findByEmail(recipientEmail);
            if (recipientUser == null) {
                throw new RuntimeException("Recipient with email " + recipientEmail + " not found.");
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


    public void revokePermission(Long documentId, Long permissionId, String token) {
        // Retrieve the document
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        User currentUser = jwtTokenProvider.getCurrentUser(token);

        if (optionalDocument.isPresent() && currentUser != null) {
            Document document = optionalDocument.get();

            // Check if the current user is the owner of the document
            if (document.getOwner().equals(currentUser)) {
                // Retrieve the permission
                Optional<DocumentPermission> optionalPermission = documentPermissionRepo.findById(permissionId);
                if (optionalPermission.isPresent()) {
                    DocumentPermission permission = optionalPermission.get();
                    // Check if the permission is associated with the document
                    if (permission.getDocument().equals(document)) {
                        documentPermissionRepo.delete(permission);
                    } else {
                        // Handle case where permission is not associated with the document
                        throw new RuntimeException("Permission with ID " + permissionId + " is not associated with the document.");
                    }
                } else {
                    // Handle case where permission is not found
                    throw new RuntimeException("Permission with ID " + permissionId + " not found.");
                }
            } else {
                // Handle case where current user is not the owner of the document
                throw new RuntimeException("You don't have permission to revoke permissions for this document.");
            }
        } else {
            // Handle case where document is not found
            throw new RuntimeException("Document with ID " + documentId + " not found.");
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

                // Check if the user has the specified permission for the document
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



    public String createDocument(DocumentDTO documentDTO, String token) {
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

        return String.valueOf(savedDocument.getId());
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
                // Owner checking
                if (document.getOwner().equals(currentUser) || document.getEditor().equals(currentUser)) {
                    document.setName(newName);
                    return documentRepo.save(document);
                }
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

                // Check if the current user is the owner of the document
                if (document.getOwner().equals(currentUser)) {
                    // Delete document permissions associated with the document
                    documentPermissionRepo.deleteByDocument(document);

                    // Delete the document itself
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

    public String openDocument(Long documentId, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        int userid = currentUser.getUserid();
        if (hasPermission(documentId, userid,PermissionType.READ)) {
            Optional<Document> optionalDocument = documentRepo.findById(documentId);
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                return document.getContent();
            }
        }
        throw new RuntimeException("You dont have permission to open it");
    }
}
