package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso ReabrirHallazgoUseCase.
 */
public class ReabrirHallazgoService implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort repositoryPort;
    private final HistorialAuditoriaPort historialPort;

    public ReabrirHallazgoService(HallazgoRepositoryPort repositoryPort, HistorialAuditoriaPort historialPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "El repositorio no puede ser nulo");
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria reabrir(HallazgoId id, String motivo) {
        Objects.requireNonNull(id, "El identificador del hallazgo no puede ser nulo");
        HallazgoAuditoria hallazgo = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Hallazgo no encontrado con ID: " + id));

        EstadoHallazgo estadoAnterior = hallazgo.reabrir();
        HallazgoAuditoria guardado = repositoryPort.guardar(hallazgo);

        String motivoRegistro = (motivo != null && !motivo.isBlank()) ? motivo : "Reapertura de hallazgo por seguimiento";
        historialPort.registrar(id, estadoAnterior, guardado.getEstado(), motivoRegistro);

        return guardado;
    }
}
