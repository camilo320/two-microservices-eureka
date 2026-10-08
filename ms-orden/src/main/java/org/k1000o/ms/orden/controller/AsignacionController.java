package org.k1000o.ms.orden.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.k1000o.ms.orden.dto.AsignacionRequest;
import org.k1000o.ms.orden.dto.AsignacionResponse;
import org.k1000o.ms.orden.service.AsignacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AsignacionController  {
    private final AsignacionService service;

    @PostMapping("{ordenId}/assign-driver")
    public ResponseEntity<AsignacionResponse> assignDriver(
            @PathVariable UUID ordenId,
            @Valid @RequestBody AsignacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.assignDriver(ordenId, request));
    }

    @PostMapping(value = "{id}/pdf", consumes = "multipart/form-data")
    public ResponseEntity<AsignacionResponse> uploadPdf(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadPdf(id, file));
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<AsignacionResponse> uploadImage(
            @PathVariable UUID id,
            @Parameter()
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadImage(id, file));
    }
}
