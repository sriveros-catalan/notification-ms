package com.sanosysalvos.notification.controller;

import com.sanosysalvos.notification.dto.CoincidenciaEventDTO;
import com.sanosysalvos.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}