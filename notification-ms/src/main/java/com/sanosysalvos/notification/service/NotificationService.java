package com.sanosysalvos.notification.service;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.dto.EstadoNotificacion;
import com.sanosysalvos.notification.dto.NotificacionResponseDTO;
import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificacionRepository repository;
    private final WebSocketNotificationService webSocketService;

    // Escucha automáticamente los mensajes de la cola RabbitMQ
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void escucharNotificacionesDesdeRabbitMQ(CoincidenciaEventDTO evento) {
        log.info("Evento recibido de RabbitMQ: {}", evento);
        procesarNotificacion(evento);
    }

    @Recover
    public void recuperar(Exception e, CoincidenciaEventDTO evento) {
        log.error("Fallo tras 3 intentos: {}", e.getMessage());
        Notificacion errorLog = new Notificacion();
        errorLog.setId_coincidencia(evento.getId_coincidencia());
        errorLog.setId_usuario(evento.getId_usuario_reporte_perdida());
        errorLog.setEstado_notificacion("ERROR");
        errorLog.setMensajeError(e.getMessage());
        repository.save(errorLog);
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void procesarNotificacion(CoincidenciaEventDTO evento) {
        log.info("Procesando coincidencia ID: {}", evento.getId_coincidencia());
        
        Notificacion n = new Notificacion();
        n.setId_coincidencia(evento.getId_coincidencia());
        n.setId_usuario(evento.getId_usuario_reporte_perdida());
        
        // Mensaje personalizado usando los datos del DTO
        n.setDescripcion("¡Match! Se ha registrado un avistamiento que podría ser tu mascota " + evento.getNombre_mascota() + "!");
        
        n.setEstado_notificacion("PENDIENTE");
        
        // Guardar en base de datos
        Notificacion notificacionGuardada = repository.save(n);
        
        log.info("Notificación guardada exitosamente para el usuario {}", evento.getId_usuario_reporte_perdida());
        
        // Enviar notificación en tiempo real vía WebSocket al usuario específico
        webSocketService.notificarUsuario(evento.getId_usuario_reporte_perdida(), notificacionGuardada);
    }

    /**
     * Obtiene las notificaciones de un usuario específico ordenadas por fecha descendente
     */
    public List<NotificacionResponseDTO> obtenerNotificacionesPorUsuario(Integer idUsuario) {
        log.info("Obteniendo notificaciones para el usuario: {}", idUsuario);
        return repository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una Notificacion a NotificacionResponseDTO
     */
    private NotificacionResponseDTO convertirADTO(Notificacion notificacion) {
        return NotificacionResponseDTO.builder()
                .id_notificacion(notificacion.getId_notificacion())
                .estado_notificacion(EstadoNotificacion.valueOf(notificacion.getEstado_notificacion()))
                .descripcion(notificacion.getDescripcion())
                .id_coincidencia(notificacion.getId_coincidencia())
                .fecha_creacion(notificacion.getFecha_creacion())
                .mensajeError(notificacion.getMensajeError())
                .build();
    }

    public List<Notificacion> obtenerTodas() {
        return repository.findAll();
    }

    public Notificacion obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }
}