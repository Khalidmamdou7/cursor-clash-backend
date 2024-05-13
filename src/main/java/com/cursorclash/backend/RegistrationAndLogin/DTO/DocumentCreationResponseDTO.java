package com.cursorclash.backend.RegistrationAndLogin.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCreationResponseDTO {
    private Long id;
    private String name;
    private int ownerId;
    private String content;
}
