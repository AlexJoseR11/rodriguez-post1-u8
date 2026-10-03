package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.port.ConteoCategoria;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.PromedioCategoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de salida de persistencia que implementa HallazgoRepositoryPort
 * y realiza el mapeo bidireccional entre el modelo de Dominio y la entidad JPA.
 */
@Component
public class HallazgoRepositoryAdapter implements HallazgoRepositoryPort {

    private final HallazgoJpaRepository jpaRepository;

    public HallazgoRepositoryAdapter(HallazgoJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "El repositorio JPA no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria guardar(HallazgoAuditoria hallazgo) {
        Objects.requireNonNull(hallazgo, "El hallazgo a persistir no puede ser nulo");
        HallazgoJpaEntity entity = aEntidad(hallazgo);
        HallazgoJpaEntity guardada = jpaRepository.save(entity);
        return aDominio(guardada);
    }

    @Override
    public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
        Objects.requireNonNull(id, "El identificador no puede ser nulo");
        return jpaRepository.findById(id.valor().toString())
                .map(this::aDominio);
    }

    @Override
    public List<HallazgoAuditoria> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::aDominio)
                .toList();
    }

    @Override
    public List<ConteoCategoria> contarPorSeveridad() {
        return jpaRepository.contarPorSeveridad();
    }

    @Override
    public List<ConteoCategoria> contarPorEstado() {
        return jpaRepository.contarPorEstado();
    }

    @Override
    public List<PromedioCategoria> promedioDiasCierrePorArea() {
        return jpaRepository.promedioDiasCierrePorArea();
    }

    private HallazgoJpaEntity aEntidad(HallazgoAuditoria dominio) {
        String planResponsable = null;
        java.time.LocalDate planFechaLimite = null;
        String planNotas = null;

        if (dominio.getPlanRemediacion() != null) {
            planResponsable = dominio.getPlanRemediacion().responsable();
            planFechaLimite = dominio.getPlanRemediacion().fechaLimite();
            planNotas = dominio.getPlanRemediacion().notas();
        }

        return new HallazgoJpaEntity(
                dominio.getId().valor().toString(),
                dominio.getTitulo(),
                dominio.getDescripcion(),
                dominio.getAreaResponsable(),
                dominio.getSeveridad().name(),
                dominio.getFechaDeteccion(),
                dominio.getEstado().name(),
                planResponsable,
                planFechaLimite,
                planNotas,
                dominio.getFechaCierre()
        );
    }

    private HallazgoAuditoria aDominio(HallazgoJpaEntity entity) {
        PlanRemediacion plan = null;
        if (entity.getPlanResponsable() != null) {
            plan = new PlanRemediacion(
                    entity.getPlanResponsable(),
                    entity.getPlanFechaLimite(),
                    entity.getPlanNotas()
            );
        }

        return HallazgoAuditoria.reconstruir(
                HallazgoId.desde(entity.getId()),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getAreaResponsable(),
                Severidad.valueOf(entity.getSeveridad()),
                entity.getFechaDeteccion(),
                EstadoHallazgo.valueOf(entity.getEstado()),
                plan,
                entity.getFechaCierre()
        );
    }
}
