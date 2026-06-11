package com.sanosysalvos.notification.service.strategy;

import com.sanosysalvos.notification.model.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Contexto del patrón STRATEGY.
 *
 * Spring inyecta automáticamente TODAS las implementaciones de
 * {@link CanalNotificacionStrategy} disponibles en el contenedor. El dispatcher
 * las indexa por canal y delega el envío a la estrategia correspondiente, sin
 * conocer los detalles de cada una.
 */
@Component
@Slf4j
public class NotificacionDispatcher {

    private final Map<CanalNotificacion, CanalNotificacionStrategy> estrategias =
            new EnumMap<>(CanalNotificacion.class);

    public NotificacionDispatcher(List<CanalNotificacionStrategy> estrategiasDisponibles) {
        for (CanalNotificacionStrategy estrategia : estrategiasDisponibles) {
            estrategias.put(estrategia.getCanal(), estrategia);
        }
        log.info("Canales de notificación registrados: {}", estrategias.keySet());
    }

    /** Entrega la notificación por un canal específico. */
    public void despachar(Notificacion notificacion, CanalNotificacion canal) {
        CanalNotificacionStrategy estrategia = estrategias.get(canal);
        if (estrategia == null) {
            log.warn("No hay estrategia registrada para el canal {}", canal);
            return;
        }
        estrategia.enviar(notificacion);
    }

    /** Entrega la notificación por todos los canales disponibles. */
    public void despacharATodos(Notificacion notificacion) {
        estrategias.values().forEach(estrategia -> estrategia.enviar(notificacion));
    }
}
