package com.cursorclash.backend.colabedit.configs;

import com.cursorclash.backend.colabedit.services.ColabEditService;
import com.cursorclash.backend.colabedit.services.ColabEditServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.LinkedHashMap;

@Component
public class WebSocketEventsHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ColabEditService colabEditService;

    LinkedHashMap<String, String> sessionDestinations = new LinkedHashMap<>();


    @EventListener
    public void handleWebSocketConnectListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String endpoint = accessor.getDestination();

        if (sessionId != null && endpoint.matches("/topic/colabedit/\\d+")) {
            // This is a colabedit session
            String documentId = endpoint.split("/")[3];
            sessionDestinations.put(sessionId, accessor.getDestination());
            var initialMessage = colabEditService.getInitialMessage(documentId);
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(StompCommand.MESSAGE.getMessageType());
            headerAccessor.setSessionId(sessionId);
            headerAccessor.setDestination(endpoint);
            messagingTemplate.convertAndSendToUser(sessionId, endpoint, initialMessage, headerAccessor.getMessageHeaders());


            broadcastMessageExceptSender("رحبوا بالحبيب صاحب السيشن دا اللي اسمه " + sessionId, sessionId, endpoint);

        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String endpoint = sessionDestinations.get(sessionId);

        // Broadcast image or any message indicating the user has disconnected
        messagingTemplate.convertAndSend(endpoint, "مبحبوش الواد دا مع السلامة اللي اسمه " + sessionId);
    }

    private void broadcastMessageExceptSender(String message, String senderSessionId, String endpoint) {
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