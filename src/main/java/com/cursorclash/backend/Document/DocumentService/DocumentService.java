package com.cursorclash.backend.Document.DocumentService;
import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import com.cursorclash.backend.RegistrationAndLogin.Repo.DocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepo documentRepo;

    public Document createDocument(String name, String content, User owner) {
        Document document = new Document();
        document.setName(name);
        document.setContent(content);
        document.setOwner(owner);
        return documentRepo.save(document);
    }

    public List<Document> listDocuments(User user) {
        return documentRepo.findByOwner(user);
    }

    public Document renameDocument(Long documentId, String newName, User requester) {
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();

            // Owner checking
            if (document.getOwner().equals(requester) || document.getEditor().equals(requester)) {
                document.setName(newName);
                return documentRepo.save(document);
            }
        }
        return null;
    }


    public void deleteDocument(Long documentId, User requester) {
        Optional<Document> optionalDocument = documentRepo.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            // Owner checking
            if (document.getOwner().equals(requester)) {
                documentRepo.delete(document);
            }
        }
    }
}
