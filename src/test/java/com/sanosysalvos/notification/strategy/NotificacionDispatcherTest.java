package com.sanosysalvos.notification.strategy;

import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.service.strategy.CanalNotificacion;
import com.sanosysalvos.notification.service.strategy.CanalNotificacionStrategy;
import com.sanosysalvos.notification.service.strategy.NotificacionDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del patrón STRATEGY usando Mockito.
 *
 * Se simulan dos estrategias (WebSocket y Email) y se verifica que el dispatcher
 * delega correctamente: a todas con despacharATodos, y solo a la indicada con despachar.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionDispatcherTest {

    @Mock
    private CanalNotificacionStrategy webSocketStrategy;

    @Mock
    private CanalNotificacionStrategy emailStrategy;

    private NotificacionDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        when(webSocketStrategy.getCanal()).thenReturn(CanalNotificacion.WEBSOCKET);
        when(emailStrategy.getCanal()).thenReturn(CanalNotificacion.EMAIL);
        dispatcher = new NotificacionDispatcher(List.of(webSocketStrategy, emailStrategy));
    }

    @Test
    @DisplayName("despacharATodos invoca enviar() en todas las estrategias")
    void despacharATodosInvocaTodas() {
        Notificacion n = new Notificacion();

        dispatcher.despacharATodos(n);

        verify(webSocketStrategy, times(1)).enviar(n);
        verify(emailStrategy, times(1)).enviar(n);
    }

    @Test
    @DisplayName("despachar(canal) invoca solo la estrategia del canal indicado")
    void despacharPorCanalInvocaSoloUna() {
        Notificacion n = new Notificacion();

        dispatcher.despachar(n, CanalNotificacion.EMAIL);

        verify(emailStrategy, times(1)).enviar(n);
        verify(webSocketStrategy, never()).enviar(n);
    }

    @Test
    @DisplayName("despachar con un canal sin estrategia registrada no lanza error ni envía")
    void despacharCanalSinEstrategiaNoFalla() {
        Notificacion n = new Notificacion();

        dispatcher.despachar(n, CanalNotificacion.PUSH); // no registrado en este test

        verify(webSocketStrategy, never()).enviar(n);
        verify(emailStrategy, never()).enviar(n);
    }
}
