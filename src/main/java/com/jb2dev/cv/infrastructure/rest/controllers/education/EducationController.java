package com.jb2dev.cv.infrastructure.rest.controllers.education;

import com.jb2dev.cv.application.education.GetEducationUseCase;
import com.jb2dev.cv.application.education.ListEducationsUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.education.EducationResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.education.EducationRestMapper;
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

@Tag(name = "Education", description = "Formal education entries.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

  private final ListEducationsUseCase listUseCase;
  private final GetEducationUseCase getByIdUseCase;
  private final EducationRestMapper mapper;

  @Operation(summary = "List formal education entries")
  @GetMapping
  public ResponseEntity<List<EducationResponse>> list() {
    return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
  }

  @Operation(summary = "Get a formal education entry by id")
  @GetMapping("/{id}")
  public ResponseEntity<EducationResponse> getById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return getByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
