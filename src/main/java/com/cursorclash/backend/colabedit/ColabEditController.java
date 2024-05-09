package com.cursorclash.backend.colabedit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/colabedit")
public class ColabEditController {

    @Autowired
    private ColabEditService colabEditService;

    @GetMapping("/")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok().body("ColabEdit is up and running");
    }

    @GetMapping("/insert-char")
    public ResponseEntity<?> insertChar(
            @RequestParam char character,
            @RequestParam double position,
            @RequestParam String documentId,
            @RequestParam int userId
    ){
        System.out.println("Inserting character " + character + " at position " + position + " in document " + documentId + " by user " + userId);
        colabEditService.insertChar(character, position, userId, documentId);
        return ResponseEntity.ok().body(colabEditService.getDocument(documentId));
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

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String broadcastNews(@Payload String message) {
        return message;
    }

    @MessageMapping("/colabedit/{documentId}")
    @SendTo("/topic/colabedit/{documentId}")
    public String broadcastColabEdit(@DestinationVariable String documentId, @Payload String message) throws JsonProcessingException {
        System.out.println("Received message " + message + " for document " + documentId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            colabEditService.handleOperations(documentId, jsonNode);

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
