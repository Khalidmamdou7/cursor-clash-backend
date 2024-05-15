package com.cursorclash.backend.colabedit.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDisconnectOpDTO {

    private String operationType = "userOffline";
    private int userId;

}