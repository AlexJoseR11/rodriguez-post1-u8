package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso ConsultarHallazgoUseCase.
 */
public class ConsultarHallazgoService implements ConsultarHallazgoUseCase {

    private final HallazgoRepositoryPort repositoryPort;

    public ConsultarHallazgoService(HallazgoRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "El repositorio no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria buscarPorId(HallazgoId id) {
        Objects.requireNonNull(id, "El identificador no puede ser nulo");
        return repositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Hallazgo no encontrado con ID: " + id));
    }

    @Override
    public List<HallazgoAuditoria> buscarTodos() {
        return repositoryPort.buscarTodos();
    }
}
