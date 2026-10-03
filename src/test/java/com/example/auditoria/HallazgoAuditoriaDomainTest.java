package com.example.auditoria;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias puras de Dominio (sin contexto de Spring Boot ni frameworks de persistencia).
 */
@DisplayName("Pruebas Unitarias de Dominio - HallazgoAuditoria y Value Objects")
class HallazgoAuditoriaDomainTest {

    private HallazgoAuditoria crearHallazgoBase() {
        return new HallazgoAuditoria(
                HallazgoId.nuevo(),
                "Vulnerabilidad en API de Pagos",
                "Falta de autenticación en endpoint crítico",
                "Seguridad TI",
                Severidad.CRITICA,
                LocalDate.now()
        );
    }

    private PlanRemediacion crearPlanValido() {
        return new PlanRemediacion("Ing. Carlos Mendoza", LocalDate.now().plusDays(15), "Implementar JWT y RBAC");
    }

    @Nested
    @DisplayName("Invariantes de Creación e Inmutabilidad de Value Objects")
    class InvariantesCreacionTests {

        @Test
        @DisplayName("Debe crear un hallazgo correctamente en estado inicial ABIERTO")
        void debeCrearHallazgoEnEstadoAbierto() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();

            assertNotNull(hallazgo.getId());
            assertEquals("Vulnerabilidad en API de Pagos", hallazgo.getTitulo());
            assertEquals("Falta de autenticación en endpoint crítico", hallazgo.getDescripcion());
            assertEquals("Seguridad TI", hallazgo.getAreaResponsable());
            assertEquals(Severidad.CRITICA, hallazgo.getSeveridad());
            assertEquals(LocalDate.now(), hallazgo.getFechaDeteccion());
            assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());
            assertNull(hallazgo.getPlanRemediacion());
            assertNull(hallazgo.getFechaCierre());
        }

        @Test
        @DisplayName("Debe fallar al instanciar HallazgoId con UUID nulo")
        void debeFallarHallazgoIdNulo() {
            assertThrows(NullPointerException.class, () -> new HallazgoId(null));
        }

        @Test
        @DisplayName("Debe fallar al instanciar PlanRemediacion con datos inválidos")
        void debeFallarPlanRemediacionInvalido() {
            assertThrows(IllegalArgumentException.class, () -> new PlanRemediacion("", LocalDate.now().plusDays(5), "Notas"));
            assertThrows(IllegalArgumentException.class, () -> new PlanRemediacion("   ", LocalDate.now().plusDays(5), "Notas"));
            assertThrows(NullPointerException.class, () -> new PlanRemediacion("Carlos", null, "Notas"));
        }

        @Test
        @DisplayName("Debe fallar al crear HallazgoAuditoria con campos obligatorios vacíos o nulos")
        void debeFallarHallazgoAuditoriaInvalido() {
            HallazgoId id = HallazgoId.nuevo();
            assertThrows(IllegalArgumentException.class, () ->
                    new HallazgoAuditoria(id, "", "Desc", "Area", Severidad.ALTA, LocalDate.now()));
            assertThrows(IllegalArgumentException.class, () ->
                    new HallazgoAuditoria(id, "Titulo", "  ", "Area", Severidad.ALTA, LocalDate.now()));
            assertThrows(IllegalArgumentException.class, () ->
                    new HallazgoAuditoria(id, "Titulo", "Desc", null, Severidad.ALTA, LocalDate.now()));
            assertThrows(NullPointerException.class, () ->
                    new HallazgoAuditoria(id, "Titulo", "Desc", "Area", null, LocalDate.now()));
        }
    }

    @Nested
    @DisplayName("Máquina de Estados y Transiciones Legales")
    class TransicionesLegalesTests {

        @Test
        @DisplayName("Debe completar el ciclo de vida completo: ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION")
        void debeCompletarCicloDeVidaValido() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();
            assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());

            // 1. Iniciar Remediación: ABIERTO -> EN_REMEDIACION
            PlanRemediacion plan = crearPlanValido();
            EstadoHallazgo prev1 = hallazgo.iniciarRemediacion(plan);
            assertEquals(EstadoHallazgo.ABIERTO, prev1);
            assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());
            assertEquals(plan, hallazgo.getPlanRemediacion());
            assertNull(hallazgo.getFechaCierre());

            // 2. Cerrar: EN_REMEDIACION -> CERRADO
            EstadoHallazgo prev2 = hallazgo.cerrar();
            assertEquals(EstadoHallazgo.EN_REMEDIACION, prev2);
            assertEquals(EstadoHallazgo.CERRADO, hallazgo.getEstado());
            assertNotNull(hallazgo.getFechaCierre());
            assertEquals(LocalDate.now(), hallazgo.getFechaCierre());

            // 3. Reabrir: CERRADO -> REABIERTO
            EstadoHallazgo prev3 = hallazgo.reabrir();
            assertEquals(EstadoHallazgo.CERRADO, prev3);
            assertEquals(EstadoHallazgo.REABIERTO, hallazgo.getEstado());
            assertNull(hallazgo.getFechaCierre(), "La fecha de cierre debe limpiarse al reabrir");

            // 4. Iniciar Remediación nuevamente: REABIERTO -> EN_REMEDIACION
            PlanRemediacion nuevoPlan = new PlanRemediacion("Ing. Laura Ríos", LocalDate.now().plusDays(10), "Parche 2.0");
            EstadoHallazgo prev4 = hallazgo.iniciarRemediacion(nuevoPlan);
            assertEquals(EstadoHallazgo.REABIERTO, prev4);
            assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());
            assertEquals(nuevoPlan, hallazgo.getPlanRemediacion());
        }

        @Test
        @DisplayName("Verificar matriz de transición del enum EstadoHallazgo")
        void verificarMatrizTransicionEnum() {
            assertTrue(EstadoHallazgo.ABIERTO.puedeTransicionarA(EstadoHallazgo.EN_REMEDIACION));
            assertFalse(EstadoHallazgo.ABIERTO.puedeTransicionarA(EstadoHallazgo.CERRADO));
            assertFalse(EstadoHallazgo.ABIERTO.puedeTransicionarA(EstadoHallazgo.REABIERTO));
            assertFalse(EstadoHallazgo.ABIERTO.puedeTransicionarA(EstadoHallazgo.ABIERTO));

            assertTrue(EstadoHallazgo.EN_REMEDIACION.puedeTransicionarA(EstadoHallazgo.CERRADO));
            assertFalse(EstadoHallazgo.EN_REMEDIACION.puedeTransicionarA(EstadoHallazgo.ABIERTO));
            assertFalse(EstadoHallazgo.EN_REMEDIACION.puedeTransicionarA(EstadoHallazgo.REABIERTO));

            assertTrue(EstadoHallazgo.CERRADO.puedeTransicionarA(EstadoHallazgo.REABIERTO));
            assertFalse(EstadoHallazgo.CERRADO.puedeTransicionarA(EstadoHallazgo.EN_REMEDIACION));
            assertFalse(EstadoHallazgo.CERRADO.puedeTransicionarA(EstadoHallazgo.ABIERTO));

            assertTrue(EstadoHallazgo.REABIERTO.puedeTransicionarA(EstadoHallazgo.EN_REMEDIACION));
            assertFalse(EstadoHallazgo.REABIERTO.puedeTransicionarA(EstadoHallazgo.CERRADO));
            assertFalse(EstadoHallazgo.REABIERTO.puedeTransicionarA(EstadoHallazgo.ABIERTO));

            assertFalse(EstadoHallazgo.ABIERTO.puedeTransicionarA(null));
        }
    }

    @Nested
    @DisplayName("Transiciones Inválidas y Restricciones de Invariantes")
    class TransicionesInvalidasTests {

        @Test
        @DisplayName("Debe lanzar TransicionInvalidaException al intentar cerrar directamente un hallazgo ABIERTO")
        void debeFallarCerrarDirectamenteDesdeAbierto() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();
            assertThrows(TransicionInvalidaException.class, hallazgo::cerrar);
        }

        @Test
        @DisplayName("Debe lanzar TransicionInvalidaException al intentar reabrir un hallazgo ABIERTO o EN_REMEDIACION")
        void debeFallarReabrirDesdeAbiertoOEnRemediacion() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();
            assertThrows(TransicionInvalidaException.class, hallazgo::reabrir);

            hallazgo.iniciarRemediacion(crearPlanValido());
            assertThrows(TransicionInvalidaException.class, hallazgo::reabrir);
        }

        @Test
        @DisplayName("Debe lanzar TransicionInvalidaException al intentar saltar de REABIERTO directamente a CERRADO")
        void debeFallarCerrarDesdeReabiertoSinRemediacion() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();
            hallazgo.iniciarRemediacion(crearPlanValido());
            hallazgo.cerrar();
            hallazgo.reabrir();

            assertEquals(EstadoHallazgo.REABIERTO, hallazgo.getEstado());
            assertThrows(TransicionInvalidaException.class, hallazgo::cerrar);
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException al iniciar remediación con plan nulo")
        void debeFallarIniciarRemediacionConPlanNulo() {
            HallazgoAuditoria hallazgo = crearHallazgoBase();
            assertThrows(IllegalArgumentException.class, () -> hallazgo.iniciarRemediacion(null));
        }
    }
}
