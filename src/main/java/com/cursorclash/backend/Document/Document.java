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


    public User getOwner() {
        return owner;
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
}