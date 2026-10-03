package com.example.auditoria.adapter.in.web.dto;

import java.time.LocalDate;

/**
 * Representación DTO inmutable del plan de remediación en las respuestas de la API REST.
 */
public record PlanRemediacionDto(
        String responsable,
        LocalDate fechaLimite,
        String notas
) {
}
