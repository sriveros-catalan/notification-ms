package com.sanosysalvos.notification.factory;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.service.factory.CoincidenciaNotificacionFactory;
import com.sanosysalvos.notification.service.factory.MascotaEncontradaNotificacionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de las fábricas concretas: cada tipo de evento debe producir una
 * notificación con mensaje y estado propios a partir del mismo DTO.
 */
class NotificacionFactoryTest {

    private CoincidenciaEventDTO eventoEjemplo() {
        CoincidenciaEventDTO evento = new CoincidenciaEventDTO();
        evento.setId_coincidencia(1L);
        evento.setId_usuario_reporte_perdida(5L);
        evento.setNombre_mascota("Luna");
        evento.setDireccion("Calle 123");
        evento.setFecha_coincidencia("2026-06-10");
        evento.setEmail_usuario("dueno@example.com");
        return evento;
    }

    @Test
    @DisplayName("CoincidenciaFactory genera mensaje de match y estado ENVIADA")
    void coincidenciaFactoryConstruyeNotificacion() {
        Notificacion n = new CoincidenciaNotificacionFactory().crear(eventoEjemplo());

        assertThat(n.getEstado_notificacion()).isEqualTo("ENVIADA");
        assertThat(n.getDescripcion()).contains("Match").contains("Luna");
        assertThat(n.getId_usuario_reporte_perdida()).isEqualTo(5L);
        assertThat(n.getEmail_usuario()).isEqualTo("dueno@example.com");
    }

    @Test
    @DisplayName("MascotaEncontradaFactory genera un mensaje distinto al de coincidencia")
    void mascotaEncontradaFactoryConstruyeNotificacionDistinta() {
        Notificacion n = new MascotaEncontradaNotificacionFactory().crear(eventoEjemplo());

        assertThat(n.getEstado_notificacion()).isEqualTo("ENVIADA");
        assertThat(n.getDescripcion()).contains("encontrada").contains("Luna");
        assertThat(n.getDescripcion()).doesNotContain("Match");
    }
}
