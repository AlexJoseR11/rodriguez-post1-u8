package com.example.auditoria.usecase.port;

import java.util.List;

/**
 * Vista consolidada del Dashboard de Auditoría.
 */
public record DashboardAuditoriaView(
        List<ConteoCategoria> porSeveridad,
        List<ConteoCategoria> porEstado,
        List<PromedioCategoria> promedioDiasCierrePorArea
) {
    public DashboardAuditoriaView {
        porSeveridad = porSeveridad == null ? List.of() : List.copyOf(porSeveridad);
        porEstado = porEstado == null ? List.of() : List.copyOf(porEstado);
        promedioDiasCierrePorArea = promedioDiasCierrePorArea == null ? List.of() : List.copyOf(promedioDiasCierrePorArea);
    }
}
