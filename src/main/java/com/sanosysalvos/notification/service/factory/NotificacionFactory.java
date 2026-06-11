package com.sanosysalvos.notification.service.factory;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;

/**
 * Patrón FACTORY METHOD.
 *
 * Define el método de fábrica {@link #crear(CoincidenciaEventDTO)} que produce
 * una {@link Notificacion} ya configurada (mensaje, estado, datos del evento).
 * Cada tipo de evento implementa su propia fábrica con su lógica de
 * construcción, separando el "qué se crea" del "cómo se usa".
 */
public interface NotificacionFactory {

    /** Tipo de evento que esta fábrica sabe construir. */
    TipoEvento getTipoEvento();

    /** Construye la notificación correspondiente a partir del evento recibido. */
    Notificacion crear(CoincidenciaEventDTO evento);
}
