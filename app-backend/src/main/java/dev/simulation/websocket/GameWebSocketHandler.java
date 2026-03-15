package dev.simulation.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handler WebSocket pour le jeu. Garde la liste des sessions et expose broadcast() pour envoyer
 * l'état du monde (ECS, positions, etc.).
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

  private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    sessions.add(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    // À brancher : actions joueur (mouvement, etc.) → ECS / Redis
    String payload = message.getPayload();
    // Exemple : broadcast à tous pour l'instant
    broadcast(payload);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    sessions.remove(session);
  }

  /** Envoie un message à tous les clients connectés (état monde, positions, events, etc.). */
  public void broadcast(String text) {
    TextMessage message = new TextMessage(text);
    for (WebSocketSession s : sessions) {
      if (s.isOpen()) {
        try {
          s.sendMessage(message);
        } catch (IOException ignored) {
          // session peut être fermée entre-temps
        }
      }
    }
  }
}
