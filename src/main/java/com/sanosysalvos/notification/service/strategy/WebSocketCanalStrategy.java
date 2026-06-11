package com.sanosysalvos.notification.service.strategy;

import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Estrategia de entrega por WebSocket.
 *
 * Reutiliza el {@link WebSocketNotificationService} existente para no duplicar
 * la lógica de envío en tiempo real.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketCanalStrategy implements CanalNotificacionStrategy {

    private final WebSocketNotificationService webSocketService;

    @Override
    public CanalNotificacion getCanal() {
        return CanalNotificacion.WEBSOCKET;
    }

    @Override
    public void enviar(Notificacion notificacion) {
        webSocketService.notificarUsuario(notificacion.getId_usuario_reporte_perdida(), notificacion);
    }
}
