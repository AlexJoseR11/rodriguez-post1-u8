package com.example.auditoria.usecase.dto;

import com.example.auditoria.domain.valueobject.Severidad;

import java.time.LocalDate;

/**
 * Comando de entrada para el registro de un nuevo hallazgo de auditoría.
 */
public record RegistrarHallazgoCommand(
        String titulo,
        String descripcion,
        String areaResponsable,
        Severidad severidad,
        LocalDate fechaDeteccion
) {
}
