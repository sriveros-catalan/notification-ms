package com.sanosysalvos.notification.controller;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.dto.NotificacionResponseDTO;
import com.sanosysalvos.notification.model.Notificacion;
import com.sanosysalvos.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("/match")
    public String enviar(@RequestBody CoincidenciaEventDTO evento) {
        service.procesarNotificacion(evento);
        return "Evento recibido correctamente";
    }

    /**
     * Obtiene las notificaciones del usuario autenticado
     * Headers requeridos: Authorization: Bearer <access_token>
     * 
     * @param idUsuario ID del usuario (se puede extraer del JWT en el BFF)
     * @return Lista de notificaciones del usuario ordenadas por fecha descendente
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNotificacionesUsuario(@PathVariable Integer idUsuario) {
        List<NotificacionResponseDTO> notificaciones = service.obtenerNotificacionesPorUsuario(idUsuario);
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping
    public List<Notificacion> obtenerTodas() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public Notificacion obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
}