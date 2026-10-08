package org.k1000o.ms.orden.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import org.k1000o.ms.orden.config.FileProperties;
import org.k1000o.ms.orden.dto.AsignacionRequest;
import org.k1000o.ms.orden.dto.AsignacionResponse;
import org.k1000o.ms.orden.dto.DriverResponse;
import org.k1000o.ms.orden.entity.Asignacion;
import org.k1000o.ms.orden.entity.Orden;
import org.k1000o.ms.orden.entity.Status;
import org.k1000o.ms.orden.feign.DriverFeign;
import org.k1000o.ms.orden.repository.AsignacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AsignacionService {

    private final AsignacionRepository repository;
    private final OrdenService ordenService;
    private final DriverFeign driverFeign;
    private final FileProperties fileProperties;

    @Transactional
    public AsignacionResponse assignDriver(UUID ordenId, AsignacionRequest request) {
        Orden orden = ordenService.getOrdenById(ordenId);

        if (orden.getStatus() != Status.CREATED) {
            throw new BadRequestException("Solamente ordenes con status CREATED pueden ser asignados");
        }
        DriverResponse driver = driverFeign.getById(request.driverId());

        if ( !driver.active()) {
            throw new BadRequestException("Driver debe ser active para ser asignado");
        }

        Asignacion assignment = Asignacion.builder()
                .orden(orden)
                .driverId(driver.id())
                .build();
        repository.save(assignment);
        return toResponse(repository.save(assignment), driver);
    }

    @Transactional
    public AsignacionResponse uploadPdf(UUID id, MultipartFile file) {
        Asignacion assignment = findAsignacionById(id);
        assignment.setPdfFileName(saveFile(file, "pdf"));
        return toResponse(repository.save(assignment),null);
    }

    @Transactional
    public AsignacionResponse uploadImage(UUID id, MultipartFile file) {
        Asignacion assignment = findAsignacionById(id);
        assignment.setImageFileName(saveFile(file, "image"));
        return toResponse(repository.save(assignment),null);
    }

    private String saveFile(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Archivo es requerido");
        }

        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String extension = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1).toLowerCase() : "";

        if ("pdf".equals(type) && !"pdf".equals(extension)) {
            throw new BadRequestException("Solamente archivos PDF estan permitidos");
        }
        if ("image".equals(type) && !("png".equals(extension) || "jpg".equals(extension) || "jpeg".equals(extension))) {
            throw new BadRequestException("Solamente archivos PNG o JPG/JPEG estan permitidos");
        }

        String fileName = UUID.randomUUID() + "." + extension;
        try {
            File targetDir = new File(fileProperties.getPath());
            if(!targetDir.exists()){
                Files.createDirectories( targetDir.toPath() );
            }
            Path target = targetDir.toPath().resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new BadRequestException("Error mientras se guardaba el archivo");
        }
    }

    public Asignacion findAsignacionById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asignacion no encontrado con id : " + id));
    }

    private AsignacionResponse toResponse(Asignacion asignacion,DriverResponse driver) {
        if( driver == null && asignacion.getDriverId() != null ) {
            driver = driverFeign.getById(asignacion.getDriverId());
        }
        return AsignacionResponse.builder()
                .id(asignacion.getId())
                .orden(ordenService.toResponse(asignacion.getOrden()))
                .driver(driver)
                .assignedAt(asignacion.getAssignedAt())
                .updatedAt(asignacion.getUpdatedAt())
                .pdfFileName(asignacion.getPdfFileName())
                .imageFileName(asignacion.getImageFileName())
                .build();
    }
}
