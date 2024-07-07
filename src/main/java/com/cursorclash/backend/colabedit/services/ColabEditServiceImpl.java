package com.cursorclash.backend.colabedit.services;

import com.cursorclash.backend.Authentication.DTOs.UserResponseDTO;
import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Document.services.DocumentService;
import com.cursorclash.backend.Document.DTOs.DocumentDTO;
import com.cursorclash.backend.colabedit.DTOs.InitialMessageOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserConnectOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserDisconnectOpDTO;
import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdt;
import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdtImpl;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class ColabEditServiceImpl implements ColabEditService {
    private LinkedHashMap<String, FractionalIndexingCrdt> crdts = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<Integer, User>> activeUsers = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<Integer, Double>> usersCursorPositions = new LinkedHashMap<>();

    @Autowired
    private DocumentService documentService;


    @Override
    public void handleOperations(String documentId, User user, JsonNode operationJson) {
        String operationType = operationJson.get("operation").asText();
        switch (operationType){
            case "insertChar":
                this.handleCharacterInsertion(documentId, user, operationJson);
                break;
            case "formatChar":
                this.handleCharacterFormatting(documentId, user, operationJson);
                break;
            case "deleteChar":
                this.handleCharacterDeletion(documentId, user, operationJson);
                break;
            case "updateCursorPosition":
                this.handleCursorPositionUpdate(documentId, user, operationJson);
                return;
            case "undo":
                this.handleUndo(documentId, user, operationJson);
                break;
            case "redo":
                this.handleRedo(documentId, user, operationJson);
                break;
            // case "moreOperations": ....
            default:
                // Unsupported operation
                return;
        }
        documentService.updateDocumentContent(
                Long.parseLong(documentId),
                crdts.get(documentId).getDocument(),
                user.getUserid()
        );
    }

    private void handleCharacterInsertion(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();

        char character = operationJson.get("insertedChar").get("char").asText().charAt(0);
        double position = operationJson.get("insertedChar").get("position").asDouble();

        var crdt = getCrdt(documentId);
        crdt.insert(position, userId, character);
    }

    private void handleCharacterFormatting(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();

        Double formattedCharPosition = operationJson.get("formattedCharPosition").asDouble();

        var formatOptions = operationJson.get("formatOptions");
        boolean isBold = formatOptions.get("isBold").asBoolean();
        boolean isItalic = formatOptions.get("isItalic").asBoolean();
        boolean isUnderline = formatOptions.get("isUnderline").asBoolean();
        // TODO: Add more formatting options

        var crdt = getCrdt(documentId);
         crdt.format(formattedCharPosition, userId, isBold, isItalic, isUnderline);

    }

    private void handleCharacterDeletion(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();

        Double deletedCharPosition = operationJson.get("deletedCharPosition").asDouble();

        var crdt = getCrdt(documentId);
        crdt.delete(deletedCharPosition, userId);
    }

    private void handleCursorPositionUpdate(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();
        Double cursorPosition = operationJson.get("cursorPosition").asDouble();

        usersCursorPositions.get(documentId).put(userId, cursorPosition);
    }

    private void handleUndo(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();

        var crdt = getCrdt(documentId);
        // TODO: implement undo
    }

    private void handleRedo(String documentId, User user, JsonNode operationJson) {
        int userId = user.getUserid();

        var crdt = getCrdt(documentId);
        // TODO: implement redo
    }

    @Override
    public void handleNewUser(String documentId, User user) {
        activeUsers.get(documentId).put(user.getUserid(), user);
        usersCursorPositions.get(documentId).put(user.getUserid(), 0.0);
    }

    @Override
    public void handleUserDisconnect(String documentId, User user) {
        activeUsers.get(documentId).remove(user.getUserid());
        usersCursorPositions.get(documentId).remove(user.getUserid());
    }

    @Override
    public InitialMessageOpDTO getInitialMessage(String documentId, User user) {
        FractionalIndexingCrdt crdt = getCrdt(documentId);

        InitialMessageOpDTO initialMessageOpDTO = new InitialMessageOpDTO();
        // TODO: send the crdt
        initialMessageOpDTO.setContent(crdt.getDocument());
        initialMessageOpDTO.setActiveUsers(activeUsers.get(documentId));
        initialMessageOpDTO.setUsersCursorsPositions(usersCursorPositions.get(documentId));
        initialMessageOpDTO.setUser(new UserResponseDTO(user.getUserid(), user.getUsername(), user.getEmail()));

        return initialMessageOpDTO;
    }

    @Override
    public UserDisconnectOpDTO getDisconnectedUserMessage(User user) {
        UserDisconnectOpDTO userDisconnectOpDTO = new UserDisconnectOpDTO();
        userDisconnectOpDTO.setUserId(user.getUserid());
        return userDisconnectOpDTO;
    }

    @Override
    public UserConnectOpDTO getConnectedUserMessage(User user) {
        UserConnectOpDTO userConnectOpDTO = new UserConnectOpDTO();
        userConnectOpDTO.setUserId(user.getUserid());
        return userConnectOpDTO;
    }


    @Override
    public boolean insertCharBetween(char c, double after, double before, int userId, String documentId) {
        // TODO: Delete this function

        var crdt = getCrdt(documentId);
        crdt.insertBetween(after, before, userId, c);
        return true;
    }

    @Override
    public String getDocument(String documentId) {
        var crdt = getCrdt(documentId);
        return crdt.getDocument();
    }

    private FractionalIndexingCrdt getCrdt(String documentId) {
        // TODO: Add exception handling
        if (!crdts.containsKey(documentId)) {
            System.out.println("no crdt is found for docID: " + documentId);
            var crdt = new FractionalIndexingCrdtImpl();
            crdts.put(documentId, crdt);
            System.out.println("Crdt have been created for docID: " + documentId);
            System.out.println(crdt);
            return crdt;
        }
        return crdts.get(documentId);


    }



}
