package org.k1000o.ms.orden.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.k1000o.ms.orden.dto.OrdenRequest;
import org.k1000o.ms.orden.dto.OrdenResponse;
import org.k1000o.ms.orden.entity.Orden;
import org.k1000o.ms.orden.entity.Status;
import org.k1000o.ms.orden.repository.OrdenRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrdenService {
    private final OrdenRepository repository;

    public OrdenResponse save(OrdenRequest req){
        return this.toResponse( repository.save(fromRequest(req) ) );
    }
    public OrdenResponse getById(UUID id){
        return this.toResponse( getOrdenById(id) );
    }
    public List<OrdenResponse> search(
            Status status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String origin,
            String destination
    ) {
        Specification<Orden> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (origin != null && !origin.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("origin")), "%" + origin.toLowerCase() + "%"));
            }
            if (destination != null && !destination.isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destination")), "%" + destination.toLowerCase() + "%"));
            }
            if (fromDate != null) {
                //Instant fromInstant = fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
            }
            if (toDate != null) {
                //Instant toInstant = toDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicates.add(criteriaBuilder.lessThan(root.get("createdAt"), toDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(specification).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrdenResponse update(UUID id, OrdenRequest request) {
        Orden orden = getOrdenById(id);

        //if( !orden.getOrigin().equals( request.origin() ) ){
            orden.setOrigin( request.origin() );
        //}
        //if( !orden.getDestination().equals( request.destination() ) ){
            orden.setDestination( request.destination() );
        //}

        return toResponse(repository.save(orden));
    }
    public Orden getOrdenById(UUID id){
        return repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("No hay orden con id : " + id));
    }
    private Orden fromRequest(OrdenRequest req){
        return Orden.builder()
                .origin(req.origin())
                .destination(req.destination())
                .build();
    }
    public OrdenResponse toResponse(Orden orden){
        return orden == null? null : OrdenResponse.builder()
                .id(orden.getId())
                .origin(orden.getOrigin())
                .destination(orden.getDestination())
                .status(orden.getStatus())
                .createdAt(orden.getCreatedAt())
                .updatedAt(orden.getUpdatedAt())
                .build();
    }
}
