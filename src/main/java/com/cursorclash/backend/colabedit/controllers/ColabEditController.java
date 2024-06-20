package com.cursorclash.backend.colabedit.controllers;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.colabedit.services.ColabEditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/colabedit")
public class ColabEditController {

    @Autowired
    private ColabEditService colabEditService;



    @GetMapping("/")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok().body("ColabEdit is up and running");
    }

    @GetMapping("/insert-char-between")
    public ResponseEntity<?> insertCharBetween(
            @RequestParam char character,
            @RequestParam double after,
            @RequestParam double before,
            @RequestParam String documentId,
            @RequestParam int userId
    ){
        System.out.println("Inserting character " + character + " between " + after + " and " + before + " in document " + documentId + " by user " + userId);
        colabEditService.insertCharBetween(character, after, before, userId, documentId);
        return ResponseEntity.ok().body(colabEditService.getDocument(documentId));
    }

    @GetMapping("/get-document")
    public ResponseEntity<?> getDocument(
            @RequestParam String documentId
    ){
        return ResponseEntity.ok().body(colabEditService.getDocument(documentId));
    }

    @GetMapping("/websocket-docs")
    @ApiResponse(responseCode = "200", description = "Returns the documentation for the websocket connection",
            content = @Content(mediaType = "text/plain"))
    public ResponseEntity<?> getMsgBrokersDocs(){
        return ResponseEntity.ok().body("To open a document, you need to open a websocket connection " +
                "on the following endpoint: /colabedit?token=<token> where token is the JWT token of the user. " +
                "The token should be passed as a query parameter. " +
                "After connecting successfully, you should subscribe to the following message queues:\n" +
                " - /topic/colabedit/<documentId> to receive updates on the document.\n" +
                " - /user/topic/colabedit/<documentId> to receive updates on the document that are specific to the user.\n" +
                " - /user/topci/sessioninfo to receive information about the session.\n" +
                "\n\nAn initial message is sent to users when they connect to the document to the" +
                " /user/topic/colabedit/<documentid> endpoint in the following format:\n" +
                "{\n" +
                "  \"operation\": \"initial\",\n" +
                "  \"content\": <documentContent>,\n" +
                "  \"activeUsers\": {\n" +
                "    <userId>: <userDetails>,\n" +
                "    ...\n" +
                "  },\n" +
                "  \"usersCursorsPositions\": {\n" +
                "    <userId>: <cursorPosition>,\n" +
                "    ...\n" +
                "  },\n" +
                "  \"user\": <userDetails>\n" +
                "}\n\n" +
                "To send messages, you should send a message to the /app/colabedit/<documentId> endpoint.\n" +
                "\n\nThe message should be a JSON object with the following structure:\n" +
                "{\n" +
                "  \"type\": \"operation\",\n" +
                "  ...operation data...\n" +
                "}\n\n" +
                "The operation data for char insertion should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"insertChar\",\n" +
                "  \"userId\": <userId>,\n" +
                "  \"insertedChar\": {\n" +
                "    \"position\": <position>,\n" +
                "    \"char\": <char>\n" +
                "  }\n" +
                "}\n" +
                "The operation data for char deletion should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"deleteChar\",\n" +
                "  \"userId\": <userId>,\n" +
                "  \"deletedCharPosition\": <position>\n" +
                "}\n" +
                "The operation data for char formatting should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"formatChar\",\n" +
                "  \"userId\": <userId>,\n" +
                "  \"formattedCharPosition\": <position>,\n" +
                "  \"formatOptions\": {\n" +
                "    \"isBold\": <isBold>,\n" +
                "    \"isItalic\": <isItalic>,\n" +
                "    \"isUnderline\": <isUnderline>\n" +
                "  }\n" +
                "}\n" +

                "The operation data for cursor position update should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"updateCursorPosition\",\n" +
                "  \"userId\": <userId>,\n" +
                "  \"cursorPosition\": <position>\n" +
                "}\n" +

                "The operation data for user disconnect should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"userOffline\",\n" +
                "  \"userId\": <userId>\n" +
                "}\n" +

                "The operation data for user connect should be in the following format:\n" +
                "{\n" +
                "  \"operation\": \"userOnline\",\n" +
                "  \"userId\": <userId>\n" +
                "}\n");

    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String broadcastNews(@Payload String message) {
        return message;
    }

    @MessageMapping("/colabedit/{documentId}")
    @SendTo("/topic/colabedit/{documentId}")
    public String broadcastColabEdit(@DestinationVariable String documentId, @Payload String message, SimpMessageHeaderAccessor accessor) throws JsonProcessingException {

        System.out.println("Received message " + message + " for document " + documentId);
        System.out.println("Session ID: " + accessor.getSessionId());
        System.out.println("Destination: " + accessor.getDestination());
        System.out.println("User: " + accessor.getUser());
        System.out.println("User details: " + accessor.getSessionAttributes().get("user"));
        User user = (User) accessor.getSessionAttributes().get("user");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            colabEditService.handleOperations(documentId, user, jsonNode);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error processing message";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown error occurred";
        }


        return message;
    }

}
