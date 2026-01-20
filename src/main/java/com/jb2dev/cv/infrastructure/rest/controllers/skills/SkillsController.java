package com.jb2dev.cv.infrastructure.rest.controllers.skills;

import com.jb2dev.cv.application.skills.*;
import com.jb2dev.cv.infrastructure.rest.dto.skills.LanguageSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.SoftSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.TechnicalSkillDetailResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.skills.SkillsRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Skills", description = "Language, technical, and soft skills.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/skills")
public class SkillsController {

    private final ListLanguageSkillsUseCase languagesUseCase;
    private final GetLanguageSkillByIdUseCase languageByIdUseCase;

    private final ListSoftSkillsUseCase softUseCase;

    private final GetTechnicalSkillsUseCase technicalUseCase;
    private final GetTechnicalSkillByNameUseCase technicalSkillByNameUseCase;
    private final GetTechnicalSkillByCategoryUseCase getTechnicalSkillByCategoryUseCase;

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

    @Operation(
            summary = "List technical skills",
            description = """
                Returns a list of technical skills. 
                You can filter the results by name or category query parameters.
                - If name is provided, returns skills matching that name.
                - If category is provided, returns skills in that category.
                - If neither is provided, returns all technical skills.
                """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of technical skills",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TechnicalSkillDetailResponse.class),
                                    examples = @ExampleObject(value = """
                                        [
                                          { "name": "Java", "category": "Language", "skill_experience": 5 },
                                          { "name": "Spring Boot", "category": "Framework", "skill_experience": 5 }
                                        ]
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No technical skills found for the given filter",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                          "error": "No technical skills found for the provided name or category."
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                          "error": "Internal server error."
                                        }
                                        """)
                            )
                    )
            },
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Filter skills by name (partial match)",
                            example = "Java"
                    ),
                    @Parameter(
                            name = "category",
                            description = "Filter skills by category (exact match)",
                            example = "Language"
                    )
            }
    )
    @GetMapping("/technical")
    public ResponseEntity<List<TechnicalSkillDetailResponse>> technicalByName(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category
    ) {
        List<TechnicalSkillDetailResponse> result;

        if (name != null) {
            result = mapper.toTechnicalDetailResponseList(technicalSkillByNameUseCase.execute(name));
        } else if (category != null) {
            result = mapper.toTechnicalDetailResponseList(getTechnicalSkillByCategoryUseCase.execute(category));
        } else {
            result = mapper.toTechnicalDetailResponseList(technicalUseCase.execute());
        }

        return result.isEmpty()
                ? ResponseEntity.status(404).body(result)
                : ResponseEntity.ok(result);
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
