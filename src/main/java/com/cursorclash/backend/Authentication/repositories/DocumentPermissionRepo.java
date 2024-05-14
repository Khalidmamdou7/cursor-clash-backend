package com.cursorclash.backend.Authentication.repositories;

import com.cursorclash.backend.Document.entities.Document;
import com.cursorclash.backend.Document.entities.DocumentPermission;
import com.cursorclash.backend.Document.DTOs.PermissionType;
import com.cursorclash.backend.Authentication.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentPermissionRepo extends JpaRepository<DocumentPermission, Long> {
    List<DocumentPermission> findByDocumentAndGranteeAndPermissionType(Document document, User grantee, PermissionType permissionType);

    void deleteByDocument(Document document);

    List<DocumentPermission> findByGrantee(User currentUser);
}
