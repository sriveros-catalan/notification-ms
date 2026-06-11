package com.sanosysalvos.notification.service.strategy;

/**
 * Canales de entrega soportados por el sistema de notificaciones.
 * Cada valor tiene asociada una {@link CanalNotificacionStrategy}.
 */
public enum CanalNotificacion {
    WEBSOCKET,
    EMAIL,
    PUSH
}
