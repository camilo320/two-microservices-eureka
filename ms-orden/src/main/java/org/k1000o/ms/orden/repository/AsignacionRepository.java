package org.k1000o.ms.orden.repository;

import org.k1000o.ms.orden.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AsignacionRepository extends JpaRepository<Asignacion, UUID>, JpaSpecificationExecutor<Asignacion> {
}
