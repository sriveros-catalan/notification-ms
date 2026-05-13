package com.sanosysalvos.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {
    private Long id_notificacion;
    private EstadoNotificacion estado_notificacion;
    private String descripcion;
    private Integer id_coincidencia;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime fecha_creacion;
    private String mensajeError;
}
