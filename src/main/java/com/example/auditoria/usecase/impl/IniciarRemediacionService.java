package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso IniciarRemediacionUseCase.
 */
public class IniciarRemediacionService implements IniciarRemediacionUseCase {

    private final HallazgoRepositoryPort repositoryPort;
    private final HistorialAuditoriaPort historialPort;

    public IniciarRemediacionService(HallazgoRepositoryPort repositoryPort, HistorialAuditoriaPort historialPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "El repositorio no puede ser nulo");
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria iniciarRemediacion(HallazgoId id, PlanRemediacion plan, String motivo) {
        Objects.requireNonNull(id, "El identificador del hallazgo no puede ser nulo");
        HallazgoAuditoria hallazgo = repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Hallazgo no encontrado con ID: " + id));

        EstadoHallazgo estadoAnterior = hallazgo.iniciarRemediacion(plan);
        HallazgoAuditoria guardado = repositoryPort.guardar(hallazgo);

        String motivoRegistro = (motivo != null && !motivo.isBlank()) ? motivo : "Inicio formal de plan de remediación";
        historialPort.registrar(id, estadoAnterior, guardado.getEstado(), motivoRegistro);

        return guardado;
    }
}
