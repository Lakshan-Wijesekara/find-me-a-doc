package com.findmeadoc.infrastructure.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// This class can be a post office
@Configuration
@EnableWebSocketMessageBroker // To turn on the STOMP Broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Setup the connection endpoint
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // This is the URL React will connect to
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS(); // Fallback option if WebSockets aren't supported
    }

    // Sort the messages and forwards to who listens to this topic port
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enables a simple in-memory broker to route messages to clients on destinations prefixed with "/topic"
        registry.enableSimpleBroker("/topic");

        // Prefix for messages sent from the frontend to the backend
        registry.setApplicationDestinationPrefixes("/app");
    }
}