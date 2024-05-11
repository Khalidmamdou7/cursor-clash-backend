package com.cursorclash.backend.RegistrationAndLogin.Repo;

import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.Document.DocumentPermission;
import com.cursorclash.backend.Document.PermissionType;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentPermissionRepo extends JpaRepository<DocumentPermission, Long> {
    List<DocumentPermission> findByDocumentAndGranteeAndPermissionType(Document document, User grantee, PermissionType permissionType);

    void deleteByDocument(Document document);
}
