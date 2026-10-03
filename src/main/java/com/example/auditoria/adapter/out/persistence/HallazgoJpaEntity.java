package com.example.auditoria.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Entidad JPA plana para la persistencia relacional del Aggregate Root HallazgoAuditoria.
 */
@Entity
@Table(name = "hallazgos")
public class HallazgoJpaEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 1000, nullable = false)
    private String descripcion;

    @Column(name = "area_responsable", nullable = false)
    private String areaResponsable;

    @Column(name = "severidad", length = 20, nullable = false)
    private String severidad;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDate fechaDeteccion;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "plan_responsable")
    private String planResponsable;

    @Column(name = "plan_fecha_limite")
    private LocalDate planFechaLimite;

    @Column(name = "plan_notas", length = 1000)
    private String planNotas;

    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;

    public HallazgoJpaEntity() {
    }

    public HallazgoJpaEntity(String id,
                             String titulo,
                             String descripcion,
                             String areaResponsable,
                             String severidad,
                             LocalDate fechaDeteccion,
                             String estado,
                             String planResponsable,
                             LocalDate planFechaLimite,
                             String planNotas,
                             LocalDate fechaCierre) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaResponsable = areaResponsable;
        this.severidad = severidad;
        this.fechaDeteccion = fechaDeteccion;
        this.estado = estado;
        this.planResponsable = planResponsable;
        this.planFechaLimite = planFechaLimite;
        this.planNotas = planNotas;
        this.fechaCierre = fechaCierre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAreaResponsable() {
        return areaResponsable;
    }

    public void setAreaResponsable(String areaResponsable) {
        this.areaResponsable = areaResponsable;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPlanResponsable() {
        return planResponsable;
    }

    public void setPlanResponsable(String planResponsable) {
        this.planResponsable = planResponsable;
    }

    public LocalDate getPlanFechaLimite() {
        return planFechaLimite;
    }

    public void setPlanFechaLimite(LocalDate planFechaLimite) {
        this.planFechaLimite = planFechaLimite;
    }

    public String getPlanNotas() {
        return planNotas;
    }

    public void setPlanNotas(String planNotas) {
        this.planNotas = planNotas;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
