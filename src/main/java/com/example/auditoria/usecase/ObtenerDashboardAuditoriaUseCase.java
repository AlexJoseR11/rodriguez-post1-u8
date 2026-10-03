package com.example.auditoria.usecase;

import com.example.auditoria.usecase.port.DashboardAuditoriaView;

/**
 * Caso de uso: Obtener métricas e indicadores consolidados para el Dashboard de Auditoría.
 */
public interface ObtenerDashboardAuditoriaUseCase {

    DashboardAuditoriaView obtenerDashboard();
}
