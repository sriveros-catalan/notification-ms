package com.sanosysalvos.notification.service.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Selector de fábricas (parte del patrón FACTORY METHOD).
 *
 * Spring inyecta todas las {@link NotificacionFactory} disponibles; el provider
 * las indexa por {@link TipoEvento} y entrega la adecuada según el evento. Si el
 * tipo es desconocido, usa COINCIDENCIA_ENCONTRADA como valor por defecto para
 * mantener compatibilidad con los mensajes existentes de RabbitMQ.
 */
@Component
@Slf4j
public class NotificacionFactoryProvider {

    private static final TipoEvento TIPO_POR_DEFECTO = TipoEvento.COINCIDENCIA_ENCONTRADA;

    private final Map<TipoEvento, NotificacionFactory> factories =
            new EnumMap<>(TipoEvento.class);

    public NotificacionFactoryProvider(List<NotificacionFactory> factoriesDisponibles) {
        for (NotificacionFactory factory : factoriesDisponibles) {
            factories.put(factory.getTipoEvento(), factory);
        }
        log.info("Fábricas de notificación registradas: {}", factories.keySet());
    }

    /** Devuelve la fábrica para el tipo indicado, o la de por defecto si no existe. */
    public NotificacionFactory obtener(TipoEvento tipo) {
        TipoEvento tipoEfectivo = (tipo != null) ? tipo : TIPO_POR_DEFECTO;
        return factories.getOrDefault(tipoEfectivo, factories.get(TIPO_POR_DEFECTO));
    }
}
