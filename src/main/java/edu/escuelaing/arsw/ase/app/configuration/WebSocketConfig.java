package edu.escuelaing.arsw.ase.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import edu.escuelaing.arsw.ase.app.controller.InvadersController;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new InvadersController(), "/ws/game").setAllowedOrigins("*");
    }
    public InvadersController invadersWebSocketHandler() {
        return new InvadersController();
    }
}