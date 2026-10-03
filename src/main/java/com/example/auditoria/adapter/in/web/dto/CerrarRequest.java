package com.example.auditoria.adapter.in.web.dto;

/**
 * DTO opcional para el motivo de cierre de un hallazgo.
 */
public record CerrarRequest(
        String motivo
) {
}
