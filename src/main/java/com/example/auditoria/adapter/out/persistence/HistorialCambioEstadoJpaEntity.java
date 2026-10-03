package com.example.auditoria.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA append-only para la bitácora legal e inmutable de cambios de estado.
 */
@Entity
@Table(name = "historial_cambios_estado")
public class HistorialCambioEstadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hallazgo_id", length = 36, nullable = false)
    private String hallazgoId;

    @Column(name = "estado_anterior", length = 30, nullable = false)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", length = 30, nullable = false)
    private String estadoNuevo;

    @Column(name = "motivo", length = 1000, nullable = false)
    private String motivo;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public HistorialCambioEstadoJpaEntity() {
    }

    public HistorialCambioEstadoJpaEntity(String hallazgoId,
                                          String estadoAnterior,
                                          String estadoNuevo,
                                          String motivo,
                                          LocalDateTime fecha) {
        this.hallazgoId = hallazgoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHallazgoId() {
        return hallazgoId;
    }

    public void setHallazgoId(String hallazgoId) {
        this.hallazgoId = hallazgoId;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
