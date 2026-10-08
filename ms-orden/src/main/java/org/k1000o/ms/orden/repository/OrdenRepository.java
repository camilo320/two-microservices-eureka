package org.k1000o.ms.orden.repository;

import org.k1000o.ms.orden.entity.Orden;
import org.k1000o.ms.orden.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrdenRepository extends JpaRepository<Orden, UUID>, JpaSpecificationExecutor<Orden> {

    List<Orden> findByStatus(Status status);
    List<Orden> findByOrigin(String origin);
    List<Orden> findByDestination(String destination);
    List<Orden> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
