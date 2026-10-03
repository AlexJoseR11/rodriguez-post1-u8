package com.example.auditoria.domain.entity;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad Raíz de Agregado (Aggregate Root) del dominio de Auditoría.
 * Encapsula la lógica de negocio, invariantes y máquina de estados para hallazgos.
 */
public class HallazgoAuditoria {

    private final HallazgoId id;
    private final String titulo;
    private final String descripcion;
    private final String areaResponsable;
    private final Severidad severidad;
    private final LocalDate fechaDeteccion;

    private EstadoHallazgo estado;
    private PlanRemediacion planRemediacion;
    private LocalDate fechaCierre;

    /**
     * Constructor para el registro de un nuevo Hallazgo de Auditoría en estado inicial ABIERTO.
     */
    public HallazgoAuditoria(HallazgoId id,
                             String titulo,
                             String descripcion,
                             String areaResponsable,
                             Severidad severidad,
                             LocalDate fechaDeteccion) {
        this.id = Objects.requireNonNull(id, "El identificador no puede ser nulo");
        this.titulo = validarTexto(titulo, "El título no puede ser nulo ni vacío");
        this.descripcion = validarTexto(descripcion, "La descripción no puede ser nula ni vacía");
        this.areaResponsable = validarTexto(areaResponsable, "El área responsable no puede ser nula ni vacía");
        this.severidad = Objects.requireNonNull(severidad, "La severidad no puede ser nula");
        this.fechaDeteccion = Objects.requireNonNull(fechaDeteccion, "La fecha de detección no puede ser nula");

        this.estado = EstadoHallazgo.ABIERTO;
        this.planRemediacion = null;
        this.fechaCierre = null;
    }

    /**
     * Constructor completo para hidratación / reconstrucción desde la capa de persistencia.
     */
    public HallazgoAuditoria(HallazgoId id,
                             String titulo,
                             String descripcion,
                             String areaResponsable,
                             Severidad severidad,
                             LocalDate fechaDeteccion,
                             EstadoHallazgo estado,
                             PlanRemediacion planRemediacion,
                             LocalDate fechaCierre) {
        this.id = Objects.requireNonNull(id, "El identificador no puede ser nulo");
        this.titulo = validarTexto(titulo, "El título no puede ser nulo ni vacío");
        this.descripcion = validarTexto(descripcion, "La descripción no puede ser nula ni vacía");
        this.areaResponsable = validarTexto(areaResponsable, "El área responsable no puede ser nula ni vacía");
        this.severidad = Objects.requireNonNull(severidad, "La severidad no puede ser nula");
        this.fechaDeteccion = Objects.requireNonNull(fechaDeteccion, "La fecha de detección no puede ser nula");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.planRemediacion = planRemediacion;
        this.fechaCierre = fechaCierre;
    }

    public static HallazgoAuditoria crear(HallazgoId id,
                                          String titulo,
                                          String descripcion,
                                          String areaResponsable,
                                          Severidad severidad,
                                          LocalDate fechaDeteccion) {
        return new HallazgoAuditoria(id, titulo, descripcion, areaResponsable, severidad, fechaDeteccion);
    }

    public static HallazgoAuditoria reconstruir(HallazgoId id,
                                                String titulo,
                                                String descripcion,
                                                String areaResponsable,
                                                Severidad severidad,
                                                LocalDate fechaDeteccion,
                                                EstadoHallazgo estado,
                                                PlanRemediacion planRemediacion,
                                                LocalDate fechaCierre) {
        return new HallazgoAuditoria(id, titulo, descripcion, areaResponsable, severidad, fechaDeteccion, estado, planRemediacion, fechaCierre);
    }

    /**
     * Inicia el proceso de remediación asociando un plan inmutable.
     * Transición permitida: ABIERTO -> EN_REMEDIACION o REABIERTO -> EN_REMEDIACION.
     *
     * @param plan Plan de remediación formal
     * @return El estado anterior a la transición
     */
    public EstadoHallazgo iniciarRemediacion(PlanRemediacion plan) {
        if (plan == null) {
            throw new IllegalArgumentException("El plan de remediación es obligatorio para iniciar la remediación");
        }
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.EN_REMEDIACION)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.EN_REMEDIACION);
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.planRemediacion = plan;
        this.estado = EstadoHallazgo.EN_REMEDIACION;
        return estadoAnterior;
    }

    /**
     * Cierra el hallazgo una vez concluida la remediación.
     * Requiere que el hallazgo esté en estado EN_REMEDIACION y cuente con plan de remediación.
     *
     * @return El estado anterior a la transición
     */
    public EstadoHallazgo cerrar() {
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.CERRADO)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.CERRADO);
        }
        if (this.planRemediacion == null) {
            throw new IllegalStateException("No se puede cerrar un hallazgo sin un plan de remediación previo");
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.estado = EstadoHallazgo.CERRADO;
        this.fechaCierre = LocalDate.now();
        return estadoAnterior;
    }

    /**
     * Reabre un hallazgo previamente cerrado ante recurrencia o auditoría de seguimiento.
     * Transición permitida: CERRADO -> REABIERTO. Limpia la fecha de cierre.
     *
     * @return El estado anterior a la transición
     */
    public EstadoHallazgo reabrir() {
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.REABIERTO)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.REABIERTO);
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.estado = EstadoHallazgo.REABIERTO;
        this.fechaCierre = null;
        return estadoAnterior;
    }

    private static String validarTexto(String texto, String mensajeError) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return texto.trim();
    }

    // Getters de lectura puros (sin setters públicos para proteger la encapsulación e invariantes)

    public HallazgoId getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getAreaResponsable() {
        return areaResponsable;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public EstadoHallazgo getEstado() {
        return estado;
    }

    public PlanRemediacion getPlanRemediacion() {
        return planRemediacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }
}
