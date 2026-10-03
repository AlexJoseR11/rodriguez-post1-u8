package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.valueobject.Severidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO para la solicitud de registro de un nuevo hallazgo.
 */
public record RegistrarHallazgoRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "El área responsable es obligatoria")
        String areaResponsable,

        @NotNull(message = "La severidad es obligatoria")
        Severidad severidad,

        LocalDate fechaDeteccion
) {
    public RegistrarHallazgoRequest {
        if (fechaDeteccion == null) {
            fechaDeteccion = LocalDate.now();
        }
    }
}
