package com.sanosysalvos.notification.service;

import com.sanosysalvos.notification.model.Notificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envía una notificación a un usuario específico via WebSocket
     * @param idUsuario - ID del usuario que reportó la mascota perdida
     * @param notificacion - Objeto con los detalles de la notificación
     */
    public void notificarUsuario(Long idUsuario, Notificacion notificacion) {
        try {
            // Construir mensaje personalizado
            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("titulo", "¡Coincidencia encontrada!");
            mensaje.put("mensaje", "Se ha encontrado una coincidencia para tu mascota");
            mensaje.put("direccion", notificacion.getDireccion());
            mensaje.put("fecha_coincidencia", notificacion.getFecha_coincidencia());
            mensaje.put("email_usuario", notificacion.getEmail_usuario());
            mensaje.put("nombre_mascota", notificacion.getNombre_mascota());
            mensaje.put("descripcion", notificacion.getDescripcion());
            mensaje.put("id_coincidencia", notificacion.getId_coincidencia());
            mensaje.put("id_usuario_reporte_perdida", notificacion.getId_usuario_reporte_perdida());

            // Enviar al usuario específico
            // El usuario debe estar conectado a /topic/notifications/{idUsuario}
            String destino = "/topic/notifications/" + idUsuario;
            messagingTemplate.convertAndSend(destino, mensaje);
            
            log.info("Notificación enviada a usuario {} vía WebSocket", idUsuario);
        } catch (Exception e) {
            log.error("Error al enviar notificación vía WebSocket al usuario {}: {}", idUsuario, e.getMessage());
        }
    }
}
