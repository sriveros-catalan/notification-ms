package com.sanosysalvos.notification.service;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.repository.NotificacionRepository;
import com.sanosysalvos.notification.service.factory.NotificacionFactory;
import com.sanosysalvos.notification.service.factory.NotificacionFactoryProvider;
import com.sanosysalvos.notification.service.factory.TipoEvento;
import com.sanosysalvos.notification.service.strategy.NotificacionDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificacionRepository repository;
    private final NotificacionFactoryProvider factoryProvider;
    private final NotificacionDispatcher dispatcher;

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
        errorLog.setId_usuario_reporte_perdida(evento.getId_usuario_reporte_perdida());
        errorLog.setNombre_mascota(evento.getNombre_mascota());
        errorLog.setDireccion(evento.getDireccion());
        errorLog.setFecha_coincidencia(evento.getFecha_coincidencia());
        errorLog.setEmail_usuario(evento.getEmail_usuario());
        errorLog.setEstado_notificacion("FALLIDA");
        errorLog.setMensajeError(e.getMessage());
        repository.save(errorLog);
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void procesarNotificacion(CoincidenciaEventDTO evento) {
        log.info("Procesando coincidencia ID: {}", evento.getId_coincidencia());

        // FACTORY METHOD: la fábrica adecuada construye la notificación según el tipo de evento.
        NotificacionFactory factory = factoryProvider.obtener(resolverTipoEvento(evento));
        Notificacion notificacion = factory.crear(evento);

        Notificacion notificacionGuardada = repository.save(notificacion);
        log.info("Notificación guardada exitosamente para el usuario {}",
                evento.getId_usuario_reporte_perdida());

        // STRATEGY: el dispatcher entrega la notificación por cada canal disponible.
        dispatcher.despacharATodos(notificacionGuardada);
    }

    /** Traduce el tipo de evento del DTO a {@link TipoEvento}; null o inválido → por defecto. */
    private TipoEvento resolverTipoEvento(CoincidenciaEventDTO evento) {
        if (evento.getTipo_evento() == null) {
            return null;
        }
        try {
            return TipoEvento.valueOf(evento.getTipo_evento().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Tipo de evento desconocido '{}', se usará el valor por defecto",
                    evento.getTipo_evento());
            return null;
        }
    }

    public List<Notificacion> obtenerTodas() {
        return repository.findAll();
    }

    public Notificacion obtenerPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }
}
