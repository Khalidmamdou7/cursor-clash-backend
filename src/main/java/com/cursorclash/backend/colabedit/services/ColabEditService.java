package com.cursorclash.backend.colabedit.services;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.colabedit.DTOs.InitialMessageOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserConnectOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserDisconnectOpDTO;
import com.fasterxml.jackson.databind.JsonNode;

public interface ColabEditService {
    public boolean insertChar(char c, double index, int userId, String documentId);
    public boolean insertCharBetween(char c, double after, double before, int userId, String documentId);
    public boolean deleteChar(double index, int userId, String documentId);
    public String getDocument(String documentId);
    public void handleOperations(String documentId, JsonNode operationJson);

    public InitialMessageOpDTO getInitialMessage(String documentId);
    public UserDisconnectOpDTO getDisconnectedUserMessage(User user);
    public UserConnectOpDTO getConnectedUserMessage(User user);
}
