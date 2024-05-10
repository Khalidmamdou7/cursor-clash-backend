package com.cursorclash.backend.RegistrationAndLogin.Service;
import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.RegistrationAndLogin.DTO.DocumentDTO;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import com.cursorclash.backend.RegistrationAndLogin.JWT.JwtTokenProvider;
import com.cursorclash.backend.RegistrationAndLogin.Repo.DocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String createDocument(DocumentDTO documentDTO, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);

        // Create a new Document entity
        Document document = new Document();
        document.setName(documentDTO.getName());
        document.setContent(documentDTO.getContent());
        document.setOwner(currentUser);

        // Save the document to the database
        Document savedDocument = documentRepo.save(document);

        return String.valueOf(savedDocument.getId());
    }



    public List<Document> listDocuments(User user) {
        return documentRepo.findByOwner(user);
    }

    public Document renameDocument(Long documentId, String newName, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            // Owner checking
            if (document.getOwner().equals(currentUser) || document.getEditor().equals(currentUser)) {
                document.setName(newName);
                return documentRepo.save(document);
            }
        }
        return null; // or throw an exception
    }


    public void deleteDocument(Long documentId, String token) {
        User currentUser = jwtTokenProvider.getCurrentUser(token);
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            // Owner checking
            if (document.getOwner().equals(currentUser)) {
                documentRepo.delete(document);
            }
        }
    }

    public String openDocument(Long documentId) {
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            return document.getContent();
        }
        return null;
    }
}
