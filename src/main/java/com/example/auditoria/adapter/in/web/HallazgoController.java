package com.example.auditoria.adapter.in.web;

import com.example.auditoria.adapter.in.web.dto.CerrarRequest;
import com.example.auditoria.adapter.in.web.dto.HallazgoResponse;
import com.example.auditoria.adapter.in.web.dto.IniciarRemediacionRequest;
import com.example.auditoria.adapter.in.web.dto.ReabrirRequest;
import com.example.auditoria.adapter.in.web.dto.RegistrarHallazgoRequest;
import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.dto.RegistrarHallazgoCommand;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.DashboardAuditoriaView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de hallazgos de auditoría.
 */
@RestController
@RequestMapping("/api/hallazgos")
public class HallazgoController {

    private final RegistrarHallazgoUseCase registrarUseCase;
    private final IniciarRemediacionUseCase iniciarRemediacionUseCase;
    private final CerrarHallazgoUseCase cerrarUseCase;
    private final ReabrirHallazgoUseCase reabrirUseCase;
    private final ConsultarHallazgoUseCase consultarUseCase;
    private final ObtenerDashboardAuditoriaUseCase dashboardUseCase;
    private final ConsultarHistorialUseCase consultarHistorialUseCase;

    public HallazgoController(RegistrarHallazgoUseCase registrarUseCase,
                              IniciarRemediacionUseCase iniciarRemediacionUseCase,
                              CerrarHallazgoUseCase cerrarUseCase,
                              ReabrirHallazgoUseCase reabrirUseCase,
                              ConsultarHallazgoUseCase consultarUseCase,
                              ObtenerDashboardAuditoriaUseCase dashboardUseCase,
                              ConsultarHistorialUseCase consultarHistorialUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.iniciarRemediacionUseCase = iniciarRemediacionUseCase;
        this.cerrarUseCase = cerrarUseCase;
        this.reabrirUseCase = reabrirUseCase;
        this.consultarUseCase = consultarUseCase;
        this.dashboardUseCase = dashboardUseCase;
        this.consultarHistorialUseCase = consultarHistorialUseCase;
    }

    @PostMapping
    public ResponseEntity<HallazgoResponse> registrar(@Valid @RequestBody RegistrarHallazgoRequest request) {
        RegistrarHallazgoCommand command = new RegistrarHallazgoCommand(
                request.titulo(),
                request.descripcion(),
                request.areaResponsable(),
                request.severidad(),
                request.fechaDeteccion()
        );
        HallazgoAuditoria hallazgo = registrarUseCase.registrar(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(HallazgoResponse.desdeDominio(hallazgo));
    }

    @PatchMapping("/{id}/iniciar-remediacion")
    public ResponseEntity<HallazgoResponse> iniciarRemediacion(@PathVariable String id,
                                                               @Valid @RequestBody IniciarRemediacionRequest request) {
        HallazgoId hallazgoId = HallazgoId.desde(id);
        PlanRemediacion plan = new PlanRemediacion(
                request.responsable(),
                request.fechaLimite(),
                request.notas()
        );
        HallazgoAuditoria hallazgo = iniciarRemediacionUseCase.iniciarRemediacion(hallazgoId, plan, request.motivo());
        return ResponseEntity.ok(HallazgoResponse.desdeDominio(hallazgo));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<HallazgoResponse> cerrar(@PathVariable String id,
                                                   @RequestBody(required = false) CerrarRequest request) {
        HallazgoId hallazgoId = HallazgoId.desde(id);
        String motivo = (request != null) ? request.motivo() : null;
        HallazgoAuditoria hallazgo = cerrarUseCase.cerrar(hallazgoId, motivo);
        return ResponseEntity.ok(HallazgoResponse.desdeDominio(hallazgo));
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<HallazgoResponse> reabrir(@PathVariable String id,
                                                    @RequestBody(required = false) ReabrirRequest request) {
        HallazgoId hallazgoId = HallazgoId.desde(id);
        String motivo = (request != null) ? request.motivo() : null;
        HallazgoAuditoria hallazgo = reabrirUseCase.reabrir(hallazgoId, motivo);
        return ResponseEntity.ok(HallazgoResponse.desdeDominio(hallazgo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallazgoResponse> buscarPorId(@PathVariable String id) {
        HallazgoId hallazgoId = HallazgoId.desde(id);
        HallazgoAuditoria hallazgo = consultarUseCase.buscarPorId(hallazgoId);
        return ResponseEntity.ok(HallazgoResponse.desdeDominio(hallazgo));
    }

    @GetMapping
    public ResponseEntity<List<HallazgoResponse>> buscarTodos() {
        List<HallazgoAuditoria> hallazgos = consultarUseCase.buscarTodos();
        List<HallazgoResponse> responses = hallazgos.stream()
                .map(HallazgoResponse::desdeDominio)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAuditoriaView> obtenerDashboard() {
        DashboardAuditoriaView dashboard = dashboardUseCase.obtenerDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<CambioEstadoView>> obtenerHistorial(@PathVariable String id) {
        HallazgoId hallazgoId = HallazgoId.desde(id);
        List<CambioEstadoView> historial = consultarHistorialUseCase.consultarHistorial(hallazgoId);
        return ResponseEntity.ok(historial);
    }
}
