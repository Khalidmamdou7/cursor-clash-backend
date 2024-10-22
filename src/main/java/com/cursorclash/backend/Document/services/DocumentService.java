package com.cursorclash.backend.Document.services;
import com.cursorclash.backend.Document.entities.Document;
import com.cursorclash.backend.Document.entities.DocumentPermission;
import com.cursorclash.backend.Document.DTOs.PermissionType;
import com.cursorclash.backend.Document.DTOs.DocumentDTO;
import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Authentication.utils.JwtTokenProvider;
import com.cursorclash.backend.Document.repositories.DocumentPermissionRepo;
import com.cursorclash.backend.Document.repositories.DocumentRepo;
import com.cursorclash.backend.exceptions.CustomExceptions.BadRequestException;
import com.cursorclash.backend.exceptions.CustomExceptions.DocumentNotFoundException;
import com.cursorclash.backend.exceptions.CustomExceptions.NotAuthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cursorclash.backend.Authentication.repositories.UserRepo;


import java.util.*;


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


        // Find the document by ID
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (!optionalDocument.isPresent()) {
            throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
        }

        Document document = optionalDocument.get();

        // Check if the current user is the owner of the document
        if (!document.getOwner().equals(currentUser)) {
            throw new NotAuthorizedException("You are not the owner of the document.");
        }

        // Find the user by email
        User recipientUser = userRepo.findByEmail(recipientEmail);
        if (recipientUser == null) {
            throw new BadRequestException("User with email " + recipientEmail + " not found.");
        }

        // Check if the same permission already exists
        List<DocumentPermission> existingPermissions = documentPermissionRepo.findByDocumentAndGranteeAndPermissionType(document, recipientUser, permissionType);
        if (!existingPermissions.isEmpty()) {
            throw new BadRequestException("The document is already shared with the user with the same permission.");
        }

        DocumentPermission documentPermission = new DocumentPermission();
        documentPermission.setDocument(document);
        documentPermission.setGrantee(recipientUser);
        documentPermission.setPermissionType(permissionType);

        documentPermissionRepo.save(documentPermission);


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
                throw new BadRequestException("User with ID " + userId + " not found.");
            }
        } else {

            throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
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
                // Owner checking
                if (document.getOwner().equals(currentUser) || document.getEditor().equals(currentUser)) {
                    document.setName(newName);
                    return documentRepo.save(document);
                } else {
                    throw new NotAuthorizedException("You are not the owner of the document.");
                }
            } else {
                throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
            }
        } else {
            throw new NotAuthorizedException("You don't have permission to rename this document.");
        }
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
                    throw new NotAuthorizedException("You are not the owner of the document.");
                }
            } else {
                throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
            }
        } else {
            throw new NotAuthorizedException("You don't have permission to delete this document.");
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
            } else {
                throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
            }
        }
        throw new NotAuthorizedException("You don't have permission to open this document.");
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

    public DocumentDTO updateDocumentContent(Long documentId, String content, int userid) {
        if (hasPermission(documentId, userid,PermissionType.WRITE)) {
            Optional<Document> optionalDocument = documentRepo.findById(documentId);
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                document.setContent(content);
                documentRepo.save(document);
                return new DocumentDTO(
                        document.getId(),
                        document.getName(),
                        document.getOwner().getUserid(),
                        document.getContent()
                );
            } else {
                throw new DocumentNotFoundException("Document with ID " + documentId + " not found.");
            }
        }
        throw new NotAuthorizedException("You don't have permission to update this document.");
    }

}
