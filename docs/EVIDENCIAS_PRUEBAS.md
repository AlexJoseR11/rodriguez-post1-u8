# Evidencias Visuales y Pruebas de Endpoints (Postman & cURL)

Este documento recopila las evidencias de ejecución y validación de los endpoints de la API REST del Sistema de Auditoría de Hallazgos (`auditoria-hallazgos`).

---

## 1. Registro de Hallazgo (POST `/api/hallazgos`)
Permite registrar un nuevo hallazgo en estado inicial `ABIERTO`.

![Creación de Hallazgo](screenshots/01_creacion_hallazgo.jpg)

### Comando cURL:
```bash
curl -X POST "http://localhost:8080/api/hallazgos" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Inyección SQL en módulo de pagos",
    "descripcion": "Sentencias concatenadas sin parametrización",
    "areaResponsable": "Core Bancario",
    "severidad": "CRITICA",
    "fechaDeteccion": "2026-10-01"
  }'
```

### Respuesta (201 Created):
```json
{
  "id": "e4f8d55c-43f1-4b1a-966f-4277df607d72",
  "titulo": "Inyección SQL en módulo de pagos",
  "descripcion": "Sentencias concatenadas sin parametrización",
  "areaResponsable": "Core Bancario",
  "severidad": "CRITICA",
  "fechaDeteccion": "2026-10-01",
  "estado": "ABIERTO",
  "planRemediacion": null,
  "fechaCierre": null
}
```

---

## 2. Ciclo de Vida: Remediación y Cierre (PATCH)

![Remediación y Cierre](screenshots/02_remediacion_cierre.jpg)

### A. Iniciar Remediación (PATCH `/api/hallazgos/{id}/iniciar-remediacion`):
```bash
curl -X PATCH "http://localhost:8080/api/hallazgos/e4f8d55c-43f1-4b1a-966f-4277df607d72/iniciar-remediacion" \
  -H "Content-Type: application/json" \
  -d '{
    "responsable": "Ing. Carlos Mendoza",
    "fechaLimite": "2026-10-20",
    "notas": "Implementación de PreparedStatement y validaciones",
    "motivo": "Asignación formal al squad de ciberseguridad"
  }'
```

### B. Cerrar Hallazgo (PATCH `/api/hallazgos/{id}/cerrar`):
```bash
curl -X PATCH "http://localhost:8080/api/hallazgos/e4f8d55c-43f1-4b1a-966f-4277df607d72/cerrar" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Pruebas SAST y DAST superadas exitosamente"
  }'
```

---

## 3. Métricas de Dashboard e Historial Forense

![Dashboard e Historial](screenshots/03_dashboard_historial.jpg)

### A. Dashboard Consolidado (GET `/api/hallazgos/dashboard`):
```bash
curl -X GET "http://localhost:8080/api/hallazgos/dashboard"
```
```json
{
  "porSeveridad": [
    { "categoria": "CRITICA", "total": 12 },
    { "categoria": "ALTA", "total": 25 },
    { "categoria": "MEDIA", "total": 40 },
    { "categoria": "BAJA", "total": 18 }
  ],
  "porEstado": [
    { "categoria": "ABIERTO", "total": 15 },
    { "categoria": "EN_REMEDIACION", "total": 30 },
    { "categoria": "CERRADO", "total": 45 },
    { "categoria": "REABIERTO", "total": 5 }
  ],
  "promedioDiasCierrePorArea": [
    { "categoria": "Core Bancario", "promedioDias": 14.5 },
    { "categoria": "Infraestructura Cloud", "promedioDias": 6.2 },
    { "categoria": "Seguridad Física", "promedioDias": 21.0 }
  ]
}
```

### B. Bitácora Append-Only de Cambios (GET `/api/hallazgos/{id}/historial`):
```bash
curl -X GET "http://localhost:8080/api/hallazgos/e4f8d55c-43f1-4b1a-966f-4277df607d72/historial"
```
```json
[
  {
    "estadoAnterior": "ABIERTO",
    "estadoNuevo": "EN_REMEDIACION",
    "motivo": "Asignación formal al squad de ciberseguridad",
    "fecha": "2026-10-03T10:15:30.123456"
  },
  {
    "estadoAnterior": "EN_REMEDIACION",
    "estadoNuevo": "CERRADO",
    "motivo": "Pruebas SAST y DAST superadas exitosamente",
    "fecha": "2026-10-03T10:20:45.654321"
  }
]
```
