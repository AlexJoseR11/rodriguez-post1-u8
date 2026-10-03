package com.example.auditoria.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object inmutable que representa el plan de remediación de un hallazgo.
 */
public record PlanRemediacion(String responsable, LocalDate fechaLimite, String notas) {

    public PlanRemediacion {
        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException("El responsable del plan de remediación no puede ser nulo ni vacío");
        }
        Objects.requireNonNull(fechaLimite, "La fecha límite del plan de remediación no puede ser nula");
        notas = notas == null ? "" : notas.trim();
    }
}
