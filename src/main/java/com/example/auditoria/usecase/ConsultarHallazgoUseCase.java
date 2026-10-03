package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;

import java.util.List;

/**
 * Caso de uso: Consultar hallazgos de auditoría de forma individual o listado completo.
 */
public interface ConsultarHallazgoUseCase {

    HallazgoAuditoria buscarPorId(HallazgoId id);

    List<HallazgoAuditoria> buscarTodos();
}
