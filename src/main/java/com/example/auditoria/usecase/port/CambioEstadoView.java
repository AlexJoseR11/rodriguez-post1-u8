package com.example.auditoria.usecase.port;

import java.time.LocalDateTime;

/**
 * Vista inmutable de un evento de cambio de estado en la bitácora de auditoría append-only.
 */
public record CambioEstadoView(
        String estadoAnterior,
        String estadoNuevo,
        String motivo,
        LocalDateTime fecha
) {
}
