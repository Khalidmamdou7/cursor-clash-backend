package com.cursorclash.backend.colabedit.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract the JWT from the query parameter
        String jwtToken = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().getFirst("token");
        System.out.println(jwtToken);
        attributes.put("token", jwtToken);
        // TODO: Authenticate the user using the JWT and store the user info in the WebSocket session attributes

        if (jwtToken != null && validateJwtToken(jwtToken)) {
            // Extract user info from the JWT
//            UserInfo userInfo = extractUserInfoFromJwt(jwtToken);
//            if (userInfo != null) {
//                // Store the user info in the WebSocket session attributes
//                attributes.put("userInfo", userInfo);
//                return true;
//            }
            return true;
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Nothing needed here for this scenario
    }

    private boolean validateJwtToken(String token) {
        // Implement your JWT validation logic here
        // Return true if the token is valid, otherwise false
        return true;  // Placeholder for demonstration
    }


}