package com.cursorclash.backend.RegistrationAndLogin.DTO;

public class DocumentDTO {
    private Long id;
    private String name;
    private String content;
    private Long ownerId;

    public DocumentDTO() {
    }

    public DocumentDTO(Long id, String name, String content, Long ownerId) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.ownerId = ownerId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
