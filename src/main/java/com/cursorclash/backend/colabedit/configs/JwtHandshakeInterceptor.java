package com.cursorclash.backend.colabedit.configs;

import com.cursorclash.backend.Authentication.entities.User;
import com.cursorclash.backend.Authentication.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.util.Map;

@Component
@ComponentScan(basePackages = "com.cursorclash.backend.Authentication.utils")
public class JwtHandshakeInterceptor implements HandshakeInterceptor {


    private JwtTokenProvider jwtTokenProvider;

    public JwtHandshakeInterceptor(@Autowired JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Extract the JWT from the query parameter
        String encodedJwtToken = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams().getFirst("token");
        if (encodedJwtToken == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        String jwtToken = URLDecoder.decode(encodedJwtToken, "UTF-8");

        if (jwtToken != null && validateJwtToken(jwtToken)) {

            User user = jwtTokenProvider.getCurrentUser(jwtToken);
            if (user != null) {
                attributes.put("user", user);
                return true;
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Nothing needed here for this scenario
    }

    private boolean validateJwtToken(String token) {
        // TODO: Implement token validation
        jwtTokenProvider.extractClaims(token);
        return true;
    }


}