package com.sanosysalvos.notification.service.strategy;

import com.sanosysalvos.notification.model.Notificacion;

/**
 * Patrón STRATEGY.
 *
 * Define el contrato común para entregar una notificación. Cada canal de envío
 * (WebSocket, Email, Push, ...) es una estrategia intercambiable que implementa
 * esta interfaz. El servicio no conoce los detalles de cada canal: solo invoca
 * {@link #enviar(Notificacion)}.
 *
 * Agregar un canal nuevo = crear una clase que implemente esta interfaz, sin
 * modificar el código que la usa (principio Open/Closed).
 */
public interface CanalNotificacionStrategy {

    /** Canal que implementa esta estrategia. */
    CanalNotificacion getCanal();

    /** Entrega la notificación por este canal. */
    void enviar(Notificacion notificacion);
}
