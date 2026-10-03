package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.usecase.port.ConteoCategoria;
import com.example.auditoria.usecase.port.PromedioCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad HallazgoJpaEntity con consultas agregadas JPQL.
 */
@Repository
public interface HallazgoJpaRepository extends JpaRepository<HallazgoJpaEntity, String> {

    @Query("SELECT new com.example.auditoria.usecase.port.ConteoCategoria(h.severidad, COUNT(h)) FROM HallazgoJpaEntity h GROUP BY h.severidad")
    List<ConteoCategoria> contarPorSeveridad();

    @Query("SELECT new com.example.auditoria.usecase.port.ConteoCategoria(h.estado, COUNT(h)) FROM HallazgoJpaEntity h GROUP BY h.estado")
    List<ConteoCategoria> contarPorEstado();

    @Query("SELECT new com.example.auditoria.usecase.port.PromedioCategoria(h.areaResponsable, AVG(function('DATEDIFF', 'DAY', h.fechaDeteccion, h.fechaCierre))) FROM HallazgoJpaEntity h WHERE h.estado = 'CERRADO' AND h.fechaCierre IS NOT NULL GROUP BY h.areaResponsable")
    List<PromedioCategoria> promedioDiasCierrePorArea();
}
