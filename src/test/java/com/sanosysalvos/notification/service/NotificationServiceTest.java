package com.sanosysalvos.notification.service;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.repository.NotificacionRepository;
import com.sanosysalvos.notification.service.factory.CoincidenciaNotificacionFactory;
import com.sanosysalvos.notification.service.factory.NotificacionFactory;
import com.sanosysalvos.notification.service.factory.NotificacionFactoryProvider;
import com.sanosysalvos.notification.service.factory.TipoEvento;
import com.sanosysalvos.notification.service.strategy.NotificacionDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas del servicio: integra FACTORY METHOD (el provider elige la fábrica que
 * crea la notificación) y STRATEGY (el dispatcher la entrega). Se simulan las
 * colaboraciones con Mockito para probar el orquestador de forma aislada.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private NotificacionFactoryProvider factoryProvider;

    @Mock
    private NotificacionDispatcher dispatcher;

    @InjectMocks
    private NotificationService service;

    private CoincidenciaEventDTO evento() {
        CoincidenciaEventDTO e = new CoincidenciaEventDTO();
        e.setId_coincidencia(1L);
        e.setId_usuario_reporte_perdida(5L);
        e.setNombre_mascota("Luna");
        e.setEmail_usuario("dueno@example.com");
        e.setTipo_evento("COINCIDENCIA_ENCONTRADA");
        return e;
    }

    @Test
    @DisplayName("procesarNotificacion usa la fábrica, guarda y despacha por los canales")
    void procesarNotificacionOrquestaFactoryYStrategy() {
        NotificacionFactory factoryReal = new CoincidenciaNotificacionFactory();
        when(factoryProvider.obtener(TipoEvento.COINCIDENCIA_ENCONTRADA)).thenReturn(factoryReal);
        when(repository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        service.procesarNotificacion(evento());

        // FACTORY: se pidió la fábrica del tipo del evento
        verify(factoryProvider, times(1)).obtener(TipoEvento.COINCIDENCIA_ENCONTRADA);
        // Persistencia
        verify(repository, times(1)).save(any(Notificacion.class));
        // STRATEGY: se despachó la notificación guardada
        verify(dispatcher, times(1)).despacharATodos(any(Notificacion.class));
    }

    @Test
    @DisplayName("La notificación despachada conserva los datos creados por la fábrica")
    void notificacionDespachadaTieneDatosDeLaFabrica() {
        when(factoryProvider.obtener(any())).thenReturn(new CoincidenciaNotificacionFactory());
        when(repository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        service.procesarNotificacion(evento());

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(dispatcher).despacharATodos(captor.capture());
        Notificacion despachada = captor.getValue();
        assertThat(despachada.getDescripcion()).contains("Luna");
        assertThat(despachada.getEstado_notificacion()).isEqualTo("ENVIADA");
    }
}
