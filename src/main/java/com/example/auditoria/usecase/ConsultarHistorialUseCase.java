package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.port.CambioEstadoView;

import java.util.List;

/**
 * Caso de uso: Consultar el historial forense de transiciones de estado de un hallazgo.
 */
public interface ConsultarHistorialUseCase {

    List<CambioEstadoView> consultarHistorial(HallazgoId hallazgoId);
}
