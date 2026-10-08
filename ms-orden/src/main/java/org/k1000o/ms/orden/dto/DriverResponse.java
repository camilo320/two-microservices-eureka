package org.k1000o.ms.orden.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DriverResponse(
        UUID id,
        String name,
        String licenseNumber,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
