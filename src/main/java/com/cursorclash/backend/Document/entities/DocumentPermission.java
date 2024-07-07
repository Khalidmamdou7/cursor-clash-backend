package com.cursorclash.backend.Document.entities;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Document.DTOs.PermissionType;
import jakarta.persistence.*;

@Entity
@Table(name = "document_permission")
public class DocumentPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne
    @JoinColumn(name = "grantee_id", nullable = false)
    private User grantee;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;

    public DocumentPermission(Document document, User grantee, PermissionType permissionType) {
        this.document = document;
        this.grantee = grantee;
        this.permissionType = permissionType;
    }

    public DocumentPermission() {
    }

    public User getGrantee() {
        return grantee;
    }

    public void setGrantee(User grantee) {
        this.grantee = grantee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }


    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
