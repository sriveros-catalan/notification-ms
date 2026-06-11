package com.sanosysalvos.notification.service.factory;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;
import org.springframework.stereotype.Component;

/**
 * Fábrica concreta para el evento MASCOTA_ENCONTRADA.
 *
 * Demuestra cómo un segundo tipo de evento produce una notificación distinta
 * (otro mensaje y estado) sin modificar el servicio ni la otra fábrica.
 */
@Component
public class MascotaEncontradaNotificacionFactory implements NotificacionFactory {

    @Override
    public TipoEvento getTipoEvento() {
        return TipoEvento.MASCOTA_ENCONTRADA;
    }

    @Override
    public Notificacion crear(CoincidenciaEventDTO evento) {
        Notificacion n = new Notificacion();
        n.setId_coincidencia(evento.getId_coincidencia());
        n.setId_usuario(evento.getId_usuario_reporte_perdida());
        n.setId_usuario_reporte_perdida(evento.getId_usuario_reporte_perdida());
        n.setNombre_mascota(evento.getNombre_mascota());
        n.setDireccion(evento.getDireccion());
        n.setFecha_coincidencia(evento.getFecha_coincidencia());
        n.setEmail_usuario(evento.getEmail_usuario());
        n.setDescripcion("¡Buenas noticias! Tu mascota " + evento.getNombre_mascota()
                + " ha sido marcada como encontrada.");
        n.setEstado_notificacion("ENVIADA");
        return n;
    }
}
