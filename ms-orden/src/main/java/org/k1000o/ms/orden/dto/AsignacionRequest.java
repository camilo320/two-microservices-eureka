package org.k1000o.ms.orden.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AsignacionRequest(
        @NotNull(message = "Driver id es requerido")
        UUID driverId
) {
}
