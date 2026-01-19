package com.jb2dev.cv.infrastructure.rest.controllers.training;

import com.jb2dev.cv.application.training.GetTrainingByIdUseCase;
import com.jb2dev.cv.application.training.ListTrainingUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.training.TrainingRestMapper;
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

@Tag(name = "Training", description = "Non-formal training entries (courses, learning paths).")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

  private final ListTrainingUseCase listUseCase;
  private final GetTrainingByIdUseCase getByIdUseCase;
  private final TrainingRestMapper mapper;

  @Operation(summary = "List training entries")
  @GetMapping
  public ResponseEntity<List<TrainingResponse>> list() {
    return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
  }

  @Operation(summary = "Get a training entry by id")
  @GetMapping("/{id}")
  public ResponseEntity<TrainingResponse> getById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return getByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
