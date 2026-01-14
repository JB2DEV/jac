package com.jb2dev.cv.infrastructure.rest.controllers.skills;

import com.jb2dev.cv.application.skills.*;
import com.jb2dev.cv.infrastructure.rest.dto.skills.LanguageSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.SoftSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.TechnicalSkillResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.skills.SkillsRestMapper;
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

@Tag(name = "Skills", description = "Language, technical, and soft skills.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/skills")
public class SkillsController {

  private final ListLanguageSkillsUseCase languagesUseCase;
  private final ListTechnicalSkillsUseCase technicalUseCase;
  private final ListSoftSkillsUseCase softUseCase;

  private final GetLanguageSkillByIdUseCase languageByIdUseCase;
  private final GetTechnicalSkillByIdUseCase technicalByIdUseCase;
  private final GetSoftSkillByIdUseCase softByIdUseCase;

  private final SkillsRestMapper mapper;

  @Operation(summary = "List language skills")
  @GetMapping("/languages")
  public ResponseEntity<List<LanguageSkillResponse>> languages() {
    return ResponseEntity.ok(mapper.toLanguageResponseList(languagesUseCase.execute()));
  }

  @Operation(summary = "Get a language skill by id")
  @GetMapping("/languages/{id}")
  public ResponseEntity<LanguageSkillResponse> languageById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return languageByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List technical skills")
  @GetMapping("/technical")
  public ResponseEntity<List<TechnicalSkillResponse>> technical() {
    return ResponseEntity.ok(mapper.toTechnicalResponseList(technicalUseCase.execute()));
  }

  @Operation(summary = "Get a technical skill by id")
  @GetMapping("/technical/{id}")
  public ResponseEntity<TechnicalSkillResponse> technicalById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return technicalByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "List soft skills")
  @GetMapping("/soft")
  public ResponseEntity<List<SoftSkillResponse>> soft() {
    return ResponseEntity.ok(mapper.toSoftResponseList(softUseCase.execute()));
  }

  @Operation(summary = "Get a soft skill by id")
  @GetMapping("/soft/{id}")
  public ResponseEntity<SoftSkillResponse> softById(
      @Parameter(description = "4-digit numeric id", example = "1234") @PathVariable int id
  ) {
    return softByIdUseCase.execute(id)
        .map(mapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
