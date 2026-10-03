package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

/**
 * Caso de uso: Cerrar un hallazgo de auditoría remediado.
 */
public interface CerrarHallazgoUseCase {

    HallazgoAuditoria cerrar(HallazgoId id, String motivo);
}
