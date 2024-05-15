package com.cursorclash.backend.colabedit.services;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.colabedit.DTOs.InitialMessageOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserConnectOpDTO;
import com.cursorclash.backend.colabedit.DTOs.UserDisconnectOpDTO;
import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdt;
import com.cursorclash.backend.colabedit.utils.FractionalIndexingCrdtImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class ColabEditServiceImpl implements ColabEditService {
    private LinkedHashMap<String, FractionalIndexingCrdt> crdts = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<Integer, ?>> activeUsers = new LinkedHashMap<>();
    private LinkedHashMap<String, LinkedHashMap<Integer, Double>> usersCursorPositions = new LinkedHashMap<>();


    @Override
    public void handleOperations(String documentId, JsonNode operationJson) {
        String operationType = operationJson.get("operation").asText();
        switch (operationType){
            case "insertChar":
                this.handleCharacterInsertion(documentId, operationJson);
                break;
            case "formatChar":
                this.handleCharacterFormatting(documentId, operationJson);
                break;
            case "deleteChar":
                this.handleCharacterDeletion(documentId, operationJson);
                break;
            case "userOnline":
                this.handleNewActiveUser(documentId, operationJson);
                break;
            case "userOffline":
                this.handleOtherUserDisconnection(documentId, operationJson);
                break;
            case "updateCursorPosition":
                this.handleCursorPositionUpdate(documentId, operationJson);
                break;
            case "undo":
                this.handleUndo(documentId, operationJson);
                break;
            case "redo":
                this.handleRedo(documentId, operationJson);
                break;
            // case "moreOperations": ....
            default:
                // Unsupported operation
                break;
        }
    }

    private void handleCharacterInsertion(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        char character = operationJson.get("insertedChar").get("char").asText().charAt(0);
        double position = operationJson.get("insertedChar").get("position").asDouble();

        var crdt = getCrdt(documentId);
        crdt.insert(position, userId, character);
    }

    private void handleCharacterFormatting(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        Double formattedCharPosition = operationJson.get("formattedCharPosition").asDouble();

        var formatOptions = operationJson.get("formatOptions");
        boolean isBold = formatOptions.get("isBold").asBoolean();
        boolean isItalic = formatOptions.get("isItalic").asBoolean();
        // TODO: Add more formatting options

        var crdt = getCrdt(documentId);
        // TODO: Implement formatting
        // crdt.format(formattedCharPosition, userId, isBold, isItalic);

    }

    private void handleCharacterDeletion(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        Double deletedCharPosition = operationJson.get("deletedCharPosition").asDouble();

        var crdt = getCrdt(documentId);
        // TODO: Implement deletion
        // crdt.deleteChar(deletedCharPosition, userId);
    }

    private void handleCursorPositionUpdate(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        Double cursorPosition = operationJson.get("cursorPosition").asDouble();

        var crdt = getCrdt(documentId);
        // TODO: Implement cursor position update
    }

    private void handleNewActiveUser(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        var userInfo = operationJson.get("userInfo");

        var crdt = getCrdt(documentId);
        // TODO: Implement changing user status

//        activeUsers.get(documentId).put(userId, userInfo);
        usersCursorPositions.get(documentId).put(userId, 0.0);
    }

    private void handleOtherUserDisconnection(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        var crdt = getCrdt(documentId);
        // TODO: Implement changing user status
    }

    private void handleUndo(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        var crdt = getCrdt(documentId);
        // TODO: implement undo
    }

    private void handleRedo(String documentId, JsonNode operationJson) {
        // TODO: userId should be string and should be authenticated by comparing with the JWT token/WSSession
        int userId = operationJson.get("userId").asInt();

        var crdt = getCrdt(documentId);
        // TODO: implement redo
    }

    @Override
    public boolean insertChar(char c, double index, int userId, String documentId) {
        // TODO: Delete this function
        var crdt = getCrdt(documentId);
        crdt.insert(index, userId, c);
        return true;
    }

    @Override
    public boolean insertCharBetween(char c, double after, double before, int userId, String documentId) {
        // TODO: Delete this function

        var crdt = getCrdt(documentId);
        crdt.insertBetween(after, before, userId, c);
        return true;
    }

    @Override
    public boolean deleteChar(double index, int userId, String documentId) {
        // TODO: Delete this function

        return false;
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

    @Override
    public InitialMessageOpDTO getInitialMessage(String documentId) {
        FractionalIndexingCrdt crdt = getCrdt(documentId);

        InitialMessageOpDTO initialMessageOpDTO = new InitialMessageOpDTO();
        // TODO: send the crdt
        initialMessageOpDTO.setContent(crdt.getDocument());
        initialMessageOpDTO.setActiveUsers(activeUsers.get(documentId));
        initialMessageOpDTO.setUsersCursorsPositions(usersCursorPositions.get(documentId));

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

}
