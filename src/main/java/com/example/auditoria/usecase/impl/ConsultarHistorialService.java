package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.util.List;
import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso ConsultarHistorialUseCase.
 */
public class ConsultarHistorialService implements ConsultarHistorialUseCase {

    private final HistorialAuditoriaPort historialPort;

    public ConsultarHistorialService(HistorialAuditoriaPort historialPort) {
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public List<CambioEstadoView> consultarHistorial(HallazgoId hallazgoId) {
        Objects.requireNonNull(hallazgoId, "El identificador del hallazgo no puede ser nulo");
        return historialPort.listarPorHallazgo(hallazgoId);
    }
}
