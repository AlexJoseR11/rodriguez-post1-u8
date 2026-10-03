package com.example.auditoria;

import com.example.auditoria.adapter.in.web.dto.CerrarRequest;
import com.example.auditoria.adapter.in.web.dto.IniciarRemediacionRequest;
import com.example.auditoria.adapter.in.web.dto.ReabrirRequest;
import com.example.auditoria.adapter.in.web.dto.RegistrarHallazgoRequest;
import com.example.auditoria.domain.valueobject.Severidad;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración MockMvc para validar endpoints REST, ciclo de vida de transiciones,
 * manejo de excepciones HTTP, dashboard consolidado y bitácora append-only.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración - HallazgoController y Flujo Completo")
class HallazgoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe registrar un hallazgo y avanzar por su ciclo de vida registrando el historial append-only")
    void debeCompletarCicloDeVidaYRegistrarHistorial() throws Exception {
        // 1. Registrar hallazgo (POST /api/hallazgos) -> 201 Created
        RegistrarHallazgoRequest registroReq = new RegistrarHallazgoRequest(
                "Inyección SQL en módulo contable",
                "Concatenación de parámetros en consulta Hibernate nativa",
                "Desarrollo Core",
                Severidad.CRITICA,
                LocalDate.now().minusDays(10)
        );

        MvcResult resultCreacion = mockMvc.perform(post("/api/hallazgos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(emptyString())))
                .andExpect(jsonPath("$.titulo", is("Inyección SQL en módulo contable")))
                .andExpect(jsonPath("$.estado", is("ABIERTO")))
                .andExpect(jsonPath("$.planRemediacion", nullValue()))
                .andExpect(jsonPath("$.fechaCierre", nullValue()))
                .andReturn();

        JsonNode jsonRes = objectMapper.readTree(resultCreacion.getResponse().getContentAsString());
        String hallazgoId = jsonRes.get("id").asText();

        // 2. Consultar por ID (GET /api/hallazgos/{id}) -> 200 OK
        mockMvc.perform(get("/api/hallazgos/" + hallazgoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(hallazgoId)))
                .andExpect(jsonPath("$.estado", is("ABIERTO")));

        // 3. Iniciar Remediación (PATCH /api/hallazgos/{id}/iniciar-remediacion) -> 200 OK
        IniciarRemediacionRequest remediacionReq = new IniciarRemediacionRequest(
                "Ing. Sofía Valenzuela",
                LocalDate.now().plusDays(7),
                "Parametrizar sentencias con PreparedStatement",
                "Asignación de sprint de seguridad"
        );

        mockMvc.perform(patch("/api/hallazgos/" + hallazgoId + "/iniciar-remediacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remediacionReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("EN_REMEDIACION")))
                .andExpect(jsonPath("$.planRemediacion.responsable", is("Ing. Sofía Valenzuela")))
                .andExpect(jsonPath("$.fechaCierre", nullValue()));

        // 4. Cerrar Hallazgo (PATCH /api/hallazgos/{id}/cerrar) -> 200 OK
        CerrarRequest cerrarReq = new CerrarRequest("Remediación verificada en pruebas de regresión SAST");
        mockMvc.perform(patch("/api/hallazgos/" + hallazgoId + "/cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cerrarReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CERRADO")))
                .andExpect(jsonPath("$.fechaCierre", not(nullValue())));

        // 5. Reabrir Hallazgo (PATCH /api/hallazgos/{id}/reabrir) -> 200 OK
        ReabrirRequest reabrirReq = new ReabrirRequest("Fuga detectada en escenario concurrente");
        mockMvc.perform(patch("/api/hallazgos/" + hallazgoId + "/reabrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reabrirReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("REABIERTO")))
                .andExpect(jsonPath("$.fechaCierre", nullValue()));

        // 6. Consultar Historial Append-Only (GET /api/hallazgos/{id}/historial) -> 200 OK
        mockMvc.perform(get("/api/hallazgos/" + hallazgoId + "/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                // Evento 1: ABIERTO -> EN_REMEDIACION
                .andExpect(jsonPath("$[0].estadoAnterior", is("ABIERTO")))
                .andExpect(jsonPath("$[0].estadoNuevo", is("EN_REMEDIACION")))
                .andExpect(jsonPath("$[0].motivo", is("Asignación de sprint de seguridad")))
                // Evento 2: EN_REMEDIACION -> CERRADO
                .andExpect(jsonPath("$[1].estadoAnterior", is("EN_REMEDIACION")))
                .andExpect(jsonPath("$[1].estadoNuevo", is("CERRADO")))
                .andExpect(jsonPath("$[1].motivo", is("Remediación verificada en pruebas de regresión SAST")))
                // Evento 3: CERRADO -> REABIERTO
                .andExpect(jsonPath("$[2].estadoAnterior", is("CERRADO")))
                .andExpect(jsonPath("$[2].estadoNuevo", is("REABIERTO")))
                .andExpect(jsonPath("$[2].motivo", is("Fuga detectada en escenario concurrente")));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request al intentar una transición no permitida por la máquina de estados")
    void debeRechazarTransicionIlegalCon400() throws Exception {
        // Crear un nuevo hallazgo (estado ABIERTO)
        RegistrarHallazgoRequest req = new RegistrarHallazgoRequest(
                "Falta de cifrado en reposo",
                "Base de datos de logs sin cifrado",
                "Infraestructura",
                Severidad.ALTA,
                LocalDate.now()
        );

        MvcResult result = mockMvc.perform(post("/api/hallazgos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String hallazgoId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // Intentar cerrar directamente sin iniciar remediación (ABIERTO -> CERRADO ilegal) -> 400 Bad Request
        mockMvc.perform(patch("/api/hallazgos/" + hallazgoId + "/cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CerrarRequest("Intento ilegal"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));

        // Intentar reabrir desde ABIERTO (ABIERTO -> REABIERTO ilegal) -> 400 Bad Request
        mockMvc.perform(patch("/api/hallazgos/" + hallazgoId + "/reabrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReabrirRequest("Intento ilegal"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request ante peticiones con datos de validación incorrectos")
    void debeRechazarValidacionesFallidasCon400() throws Exception {
        // Enviar request con campos obligatorios vacíos
        String bodyInvalido = """
                {
                    "titulo": "",
                    "descripcion": "",
                    "areaResponsable": "",
                    "severidad": null
                }
                """;

        mockMvc.perform(post("/api/hallazgos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("Debe retornar 404 Not Found al buscar un hallazgo inexistente")
    void debeRetornar404ParaHallazgoInexistente() throws Exception {
        String uuidInexistente = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/hallazgos/" + uuidInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("Debe consultar el Dashboard consolidado de auditoría exitosamente")
    void debeObtenerDashboardConsolidado() throws Exception {
        // Crear y cerrar un hallazgo para generar métricas
        RegistrarHallazgoRequest regReq = new RegistrarHallazgoRequest(
                "Contraseña débil en admin",
                "Uso de credenciales por defecto",
                "Ciberseguridad",
                Severidad.CRITICA,
                LocalDate.now().minusDays(5)
        );

        MvcResult result = mockMvc.perform(post("/api/hallazgos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // Iniciar remediación y cerrar
        mockMvc.perform(patch("/api/hallazgos/" + id + "/iniciar-remediacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IniciarRemediacionRequest(
                                "Auditor Seguridad", LocalDate.now().plusDays(2), "Reset clave", "Inmediato"
                        ))))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/hallazgos/" + id + "/cerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CerrarRequest("Clave restablecida"))))
                .andExpect(status().isOk());

        // Consultar dashboard consolidado (GET /api/hallazgos/dashboard)
        mockMvc.perform(get("/api/hallazgos/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porSeveridad", not(nullValue())))
                .andExpect(jsonPath("$.porEstado", not(nullValue())))
                .andExpect(jsonPath("$.promedioDiasCierrePorArea", not(nullValue())))
                .andExpect(jsonPath("$.porSeveridad.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.porEstado.length()", greaterThanOrEqualTo(1)));
    }
}
