package com.cursorclash.backend.colabedit.configs;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Document.services.DocumentService;
import com.cursorclash.backend.Document.DTOs.PermissionType;
import com.cursorclash.backend.colabedit.services.ColabEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import java.util.LinkedHashMap;

@Component
public class WebSocketEventsHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ColabEditService colabEditService;

    @Autowired
    private DocumentService documentService;

    LinkedHashMap<String, String> sessionDestinations = new LinkedHashMap<>();


    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String endpoint = accessor.getDestination();

        if (sessionId != null && endpoint.matches("/topic/colabedit/\\d+")) {
            // This is a colabedit session
            String documentId = endpoint.split("/")[3];
            User user = (User) accessor.getSessionAttributes().get("user");
            // TODO: Check if user is allowed to access this document
            if (documentService.hasPermission(Long.valueOf(documentId), user.getUserid(), PermissionType.WRITE)) {
                sessionDestinations.put(sessionId, accessor.getDestination());
                var initialMessage = colabEditService.getInitialMessage(documentId, user);
                SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(StompCommand.MESSAGE.getMessageType());
                headerAccessor.setSessionId(sessionId);
                headerAccessor.setDestination(endpoint);
                messagingTemplate.convertAndSendToUser(sessionId, endpoint, initialMessage, headerAccessor.getMessageHeaders());

                var userConnectMessage = colabEditService.getConnectedUserMessage(user);
                colabEditService.handleNewUser(documentId, user);

                broadcastMessageExceptSender(userConnectMessage, sessionId, endpoint);
            }
            else {
                throw new RuntimeException("You dont have permission to Edit it");
            }
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        User user = (User) accessor.getSessionAttributes().get("user");
        String endpoint = sessionDestinations.get(sessionId);

        var disconnectedUserMessage = colabEditService.getDisconnectedUserMessage(user);

        messagingTemplate.convertAndSend(endpoint, disconnectedUserMessage);
    }

    private void broadcastMessageExceptSender(Object message, String senderSessionId, String endpoint) {
        var endpointSessions = sessionDestinations.entrySet().stream().filter(entry -> entry.getValue().equals(endpoint));
        endpointSessions.forEach(entry -> {
            if (!entry.getKey().equals(senderSessionId)) {
                SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(StompCommand.MESSAGE.getMessageType());
                String sessionId = entry.getKey();
                headerAccessor.setSessionId(sessionId);
                headerAccessor.setDestination(endpoint);
                messagingTemplate.convertAndSendToUser(sessionId, endpoint, message, headerAccessor.getMessageHeaders());
            }
        });
    }



}
