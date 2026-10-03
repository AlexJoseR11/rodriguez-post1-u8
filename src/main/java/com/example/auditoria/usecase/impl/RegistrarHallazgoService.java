package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.dto.RegistrarHallazgoCommand;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

/**
 * Implementación POJO pura del caso de uso RegistrarHallazgoUseCase.
 */
public class RegistrarHallazgoService implements RegistrarHallazgoUseCase {

    private final HallazgoRepositoryPort repositoryPort;

    public RegistrarHallazgoService(HallazgoRepositoryPort repositoryPort) {
        this.repositoryPort = Objects.requireNonNull(repositoryPort, "El repositorio no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria registrar(RegistrarHallazgoCommand command) {
        Objects.requireNonNull(command, "El comando no puede ser nulo");
        HallazgoId id = HallazgoId.nuevo();
        HallazgoAuditoria nuevoHallazgo = new HallazgoAuditoria(
                id,
                command.titulo(),
                command.descripcion(),
                command.areaResponsable(),
                command.severidad(),
                command.fechaDeteccion()
        );
        return repositoryPort.guardar(nuevoHallazgo);
    }
}
