package com.example.auditoria.usecase.port;

/**
 * Proyección agregada para conteo de hallazgos por categoría (Severidad o Estado).
 */
public record ConteoCategoria(String categoria, long total) {
}
