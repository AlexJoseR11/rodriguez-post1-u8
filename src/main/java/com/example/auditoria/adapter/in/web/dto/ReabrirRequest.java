package com.example.auditoria.adapter.in.web.dto;

/**
 * DTO para la solicitud de reapertura de un hallazgo cerrado.
 */
public record ReabrirRequest(
        String motivo
) {
}
