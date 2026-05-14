package com.sanosysalvos.notification.dto;

import lombok.Data;

@Data
public class CoincidenciaEventDTO {
    private Long id_coincidencia;
    private Long id_usuario_reporte_perdida;
    private String nombre_mascota;
    private String tipo_mascota;
    private String direccion;
    private String fecha_coincidencia;
    private String email_usuario;
    private String fotoMascota;
}