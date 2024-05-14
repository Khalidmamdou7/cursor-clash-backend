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
public class InitialMessageOpDTO {

    private String operationType = "initial";
    private String content;
    private LinkedHashMap<Integer, ?> activeUsers;
    private LinkedHashMap<Integer, ?> usersCursorsPositions;

}
