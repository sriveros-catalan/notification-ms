package com.sanosysalvos.notification.service.factory;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;
import org.springframework.stereotype.Component;

/**
 * Fábrica concreta para el evento COINCIDENCIA_ENCONTRADA.
 *
 * Reproduce la notificación de "match" que antes se construía a mano dentro del
 * servicio.
 */
@Component
public class CoincidenciaNotificacionFactory implements NotificacionFactory {

    @Override
    public TipoEvento getTipoEvento() {
        return TipoEvento.COINCIDENCIA_ENCONTRADA;
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
        n.setDescripcion("¡Match! Se ha registrado un avistamiento que podría ser tu mascota "
                + evento.getNombre_mascota() + "!");
        n.setEstado_notificacion("ENVIADA");
        return n;
    }
}
