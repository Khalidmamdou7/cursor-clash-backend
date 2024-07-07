package com.cursorclash.backend.colabedit.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserConnectOpDTO {

    private String operationType = "userOnline";
    private int userId;

}