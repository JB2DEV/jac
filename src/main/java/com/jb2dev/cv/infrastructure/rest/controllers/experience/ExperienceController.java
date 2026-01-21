package com.jb2dev.cv.infrastructure.rest.controllers.experience;

import com.jb2dev.cv.application.experience.GetExperienceUseCase;
import com.jb2dev.cv.application.experience.ListExperiencesUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.experience.ExperienceRestMapper;
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

@Tag(name = "Experience", description = "Work experience entries.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

  private final ListExperiencesUseCase listUseCase;
  private final GetExperienceUseCase getByIdUseCase;
  private final ExperienceRestMapper mapper;

  @Operation(summary = "List experience entries")
  @GetMapping
  public ResponseEntity<List<ExperienceResponse>> list() {
    return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
  }

  @Operation(summary = "Get an experience entry by id")
  @GetMapping("/{id}")
  public ResponseEntity<ExperienceResponse> getById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return getByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
