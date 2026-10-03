package com.example.auditoria.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador único inmutable para un Hallazgo de Auditoría.
 */
public record HallazgoId(UUID valor) {

    public HallazgoId {
        Objects.requireNonNull(valor, "El valor del UUID no puede ser nulo");
    }

    public static HallazgoId nuevo() {
        return new HallazgoId(UUID.randomUUID());
    }

    public static HallazgoId desde(String idStr) {
        Objects.requireNonNull(idStr, "El identificador en texto no puede ser nulo");
        return new HallazgoId(UUID.fromString(idStr));
    }

    public static HallazgoId de(UUID uuid) {
        return new HallazgoId(uuid);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
