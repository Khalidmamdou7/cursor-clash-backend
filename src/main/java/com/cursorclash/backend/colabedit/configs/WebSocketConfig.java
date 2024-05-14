package com.cursorclash.backend.colabedit.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        JwtHandshakeInterceptor jwtHandshakeInterceptor = new JwtHandshakeInterceptor();

        String originPattern = "http*";

        registry.addEndpoint("/chat").withSockJS();
        registry.addEndpoint("/colabedit").addInterceptors(jwtHandshakeInterceptor).setAllowedOriginPatterns(originPattern).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }






}