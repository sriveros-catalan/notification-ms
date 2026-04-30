package com.sanosysalvos.notification.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notificacion;
    private String estado_notificacion; 
    private String descripcion;
    private Integer id_coincidencia;
    private Integer id_usuario;
    private LocalDateTime fecha_creacion = LocalDateTime.now();
    private String mensajeError;
}