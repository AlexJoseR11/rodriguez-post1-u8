package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.Severidad;

import java.time.LocalDate;

/**
 * DTO de respuesta para representar un hallazgo de auditoría en la capa web.
 */
public record HallazgoResponse(
        String id,
        String titulo,
        String descripcion,
        String areaResponsable,
        Severidad severidad,
        LocalDate fechaDeteccion,
        EstadoHallazgo estado,
        PlanRemediacionDto planRemediacion,
        LocalDate fechaCierre
) {
    public static HallazgoResponse desdeDominio(HallazgoAuditoria hallazgo) {
        PlanRemediacionDto planDto = null;
        if (hallazgo.getPlanRemediacion() != null) {
            planDto = new PlanRemediacionDto(
                    hallazgo.getPlanRemediacion().responsable(),
                    hallazgo.getPlanRemediacion().fechaLimite(),
                    hallazgo.getPlanRemediacion().notas()
            );
        }
        return new HallazgoResponse(
                hallazgo.getId().valor().toString(),
                hallazgo.getTitulo(),
                hallazgo.getDescripcion(),
                hallazgo.getAreaResponsable(),
                hallazgo.getSeveridad(),
                hallazgo.getFechaDeteccion(),
                hallazgo.getEstado(),
                planDto,
                hallazgo.getFechaCierre()
        );
    }
}
