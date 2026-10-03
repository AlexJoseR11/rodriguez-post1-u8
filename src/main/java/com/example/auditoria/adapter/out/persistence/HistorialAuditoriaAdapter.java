package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Adaptador de salida para la bitácora legal append-only implementando HistorialAuditoriaPort.
 */
@Component
public class HistorialAuditoriaAdapter implements HistorialAuditoriaPort {

    private final HistorialCambioEstadoJpaRepository jpaRepository;

    public HistorialAuditoriaAdapter(HistorialCambioEstadoJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "El repositorio de historial no puede ser nulo");
    }

    @Override
    public void registrar(HallazgoId hallazgoId, EstadoHallazgo anterior, EstadoHallazgo nuevo, String motivo) {
        Objects.requireNonNull(hallazgoId, "El ID del hallazgo no puede ser nulo");
        Objects.requireNonNull(anterior, "El estado anterior no puede ser nulo");
        Objects.requireNonNull(nuevo, "El estado nuevo no puede ser nulo");

        HistorialCambioEstadoJpaEntity entidad = new HistorialCambioEstadoJpaEntity(
                hallazgoId.valor().toString(),
                anterior.name(),
                nuevo.name(),
                motivo != null ? motivo : "",
                LocalDateTime.now()
        );
        jpaRepository.save(entidad);
    }

    @Override
    public List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId) {
        Objects.requireNonNull(hallazgoId, "El ID del hallazgo no puede ser nulo");
        return jpaRepository.findByHallazgoIdOrderByFechaAsc(hallazgoId.valor().toString())
                .stream()
                .map(entity -> new CambioEstadoView(
                        entity.getEstadoAnterior(),
                        entity.getEstadoNuevo(),
                        entity.getMotivo(),
                        entity.getFecha()
                ))
                .toList();
    }
}
