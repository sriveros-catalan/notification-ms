package com.sanosysalvos.notification.service.strategy;

import com.sanosysalvos.notification.model.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Estrategia de entrega por notificación Push.
 *
 * Stub de demostración: registra el envío en el log. Para producción se
 * integraría aquí Firebase Cloud Messaging u otro proveedor de push.
 */
@Component
@Slf4j
public class PushCanalStrategy implements CanalNotificacionStrategy {

    @Override
    public CanalNotificacion getCanal() {
        return CanalNotificacion.PUSH;
    }

    @Override
    public void enviar(Notificacion notificacion) {
        log.info("[PUSH] Enviando a usuario {}: {}",
                notificacion.getId_usuario_reporte_perdida(), notificacion.getDescripcion());
    }
}
