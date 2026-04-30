package com.sanosysalvos.notification.dto;

import lombok.Data;

@Data
public class CoincidenciaEventDTO {
    private Integer id_coincidencia;
    private Integer id_usuario_reporte_perdida;
    private String nombre_mascota;
    private String email_usuario;
}