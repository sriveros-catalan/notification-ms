package com.sanosysalvos.notification.service.strategy;

import com.sanosysalvos.notification.model.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Estrategia de entrega por Email.
 *
 * Stub de demostración: registra el envío en el log. Para producción se
 * inyectaría aquí un JavaMailSender (spring-boot-starter-mail) sin tocar el
 * resto del sistema, gracias al patrón Strategy.
 */
@Component
@Slf4j
public class EmailCanalStrategy implements CanalNotificacionStrategy {

    @Override
    public CanalNotificacion getCanal() {
        return CanalNotificacion.EMAIL;
    }

    @Override
    public void enviar(Notificacion notificacion) {
        log.info("[EMAIL] Enviando a {}: {}",
                notificacion.getEmail_usuario(), notificacion.getDescripcion());
    }
}
