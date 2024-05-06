package com.cursorclash.backend.Document;

import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;

    @OneToMany(mappedBy = "document")
    private List<DocumentPermission> permissions;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    public User getOwner() {
        return owner;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DocumentPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<DocumentPermission> permissions) {
        this.permissions = permissions;
    }

    public User getEditor() {
        if (permissions != null) {
            for (DocumentPermission permission : permissions) {
                if (permission.getPermissionType() == PermissionType.WRITE) {
                    return permission.getUser();
                }
            }
        }
        return null;
    }
}