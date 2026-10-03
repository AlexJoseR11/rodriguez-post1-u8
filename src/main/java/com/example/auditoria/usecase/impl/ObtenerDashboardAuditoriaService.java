package com.example.auditoria.usecase.impl;

import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.port.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso ObtenerDashboardAuditoriaUseCase.
 */
public class ObtenerDashboardAuditoriaService implements ObtenerDashboardAuditoriaUseCase {

    private final HallazgoRepositoryPort repositoryPort;

    public ObtenerDashboardAuditoriaService(HallazgoRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "El repositorio no puede ser nulo");
    }

    @Override
    public DashboardAuditoriaView obtenerDashboard() {
        return new DashboardAuditoriaView(
                repositoryPort.contarPorSeveridad(),
                repositoryPort.contarPorEstado(),
                repositoryPort.promedioDiasCierrePorArea()
        );
    }
}
