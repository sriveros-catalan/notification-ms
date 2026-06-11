package com.sanosysalvos.notification.factory;

import com.sanosysalvos.notification.service.factory.CoincidenciaNotificacionFactory;
import com.sanosysalvos.notification.service.factory.MascotaEncontradaNotificacionFactory;
import com.sanosysalvos.notification.service.factory.NotificacionFactory;
import com.sanosysalvos.notification.service.factory.NotificacionFactoryProvider;
import com.sanosysalvos.notification.service.factory.TipoEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas del patrón FACTORY METHOD: el provider debe elegir la fábrica correcta
 * según el tipo de evento y usar la de por defecto cuando el tipo es null o inválido.
 */
class NotificacionFactoryProviderTest {

    private NotificacionFactoryProvider provider;

    @BeforeEach
    void setUp() {
        List<NotificacionFactory> factories = List.of(
                new CoincidenciaNotificacionFactory(),
                new MascotaEncontradaNotificacionFactory());
        provider = new NotificacionFactoryProvider(factories);
    }

    @Test
    @DisplayName("Devuelve la fábrica de coincidencia para COINCIDENCIA_ENCONTRADA")
    void devuelveFabricaCoincidencia() {
        NotificacionFactory factory = provider.obtener(TipoEvento.COINCIDENCIA_ENCONTRADA);
        assertThat(factory).isInstanceOf(CoincidenciaNotificacionFactory.class);
    }

    @Test
    @DisplayName("Devuelve la fábrica de mascota encontrada para MASCOTA_ENCONTRADA")
    void devuelveFabricaMascotaEncontrada() {
        NotificacionFactory factory = provider.obtener(TipoEvento.MASCOTA_ENCONTRADA);
        assertThat(factory).isInstanceOf(MascotaEncontradaNotificacionFactory.class);
    }

    @Test
    @DisplayName("Usa la fábrica por defecto (coincidencia) cuando el tipo es null")
    void usaFabricaPorDefectoSiTipoNull() {
        NotificacionFactory factory = provider.obtener(null);
        assertThat(factory).isInstanceOf(CoincidenciaNotificacionFactory.class);
    }
}
