package com.jb2dev.cv.infrastructure.rest.controllers.certifications;

import com.jb2dev.cv.application.certifications.GetCertificationUseCase;
import com.jb2dev.cv.application.certifications.ListCertificationsUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.certifications.CertificationResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.certifications.CertificationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Certifications", description = "Licenses and certifications.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationsController {

  private final ListCertificationsUseCase listUseCase;
  private final GetCertificationUseCase getByIdUseCase;
  private final CertificationRestMapper mapper;

  @Operation(summary = "List licenses and certifications")
  @GetMapping
  public ResponseEntity<List<CertificationResponse>> list() {
    return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
  }

  @Operation(summary = "Get a license/certification by id")
  @GetMapping("/{id}")
  public ResponseEntity<CertificationResponse> getById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return getByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
