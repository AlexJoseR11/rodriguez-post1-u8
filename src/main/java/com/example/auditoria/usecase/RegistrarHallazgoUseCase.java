package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.usecase.dto.RegistrarHallazgoCommand;

/**
 * Caso de uso: Registrar un nuevo hallazgo de auditoría.
 */
public interface RegistrarHallazgoUseCase {

    HallazgoAuditoria registrar(RegistrarHallazgoCommand command);
}
