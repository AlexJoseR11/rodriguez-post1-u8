package com.example.auditoria.domain.valueobject;

/**
 * Excepción de dominio lanzada cuando se intenta una transición de estado no permitida
 * por la máquina de estados del hallazgo de auditoría.
 */
public class TransicionInvalidaException extends RuntimeException {

    private final EstadoHallazgo estadoOrigen;
    private final EstadoHallazgo estadoDestino;

    public TransicionInvalidaException(EstadoHallazgo estadoOrigen, EstadoHallazgo estadoDestino) {
        super(String.format("Transición de estado inválida: no es posible transicionar de %s a %s", estadoOrigen, estadoDestino));
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
    }

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
        this.estadoOrigen = null;
        this.estadoDestino = null;
    }

    public EstadoHallazgo getEstadoOrigen() {
        return estadoOrigen;
    }

    public EstadoHallazgo getEstadoDestino() {
        return estadoDestino;
    }
}
