package com.sanosysalvos.notification.service.factory;

/**
 * Tipos de evento que pueden generar una notificación. Cada tipo tiene una
 * {@link NotificacionFactory} que sabe cómo construir su notificación.
 */
public enum TipoEvento {
    /** Se encontró un avistamiento que coincide con una mascota perdida. */
    COINCIDENCIA_ENCONTRADA,
    /** Una mascota reportada como perdida fue marcada como encontrada. */
    MASCOTA_ENCONTRADA
}
