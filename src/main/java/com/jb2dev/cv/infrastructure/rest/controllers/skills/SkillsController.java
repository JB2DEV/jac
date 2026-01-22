package com.jb2dev.cv.infrastructure.rest.controllers.skills;

import com.jb2dev.cv.application.skills.*;
import com.jb2dev.cv.application.skills.query.TechnicalSkillSearchCriteria;
import com.jb2dev.cv.infrastructure.rest.dto.skills.LanguageSkillDetailResponse;
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
    private final GetLanguageSkillUseCase languageByIdUseCase;

    private final ListSoftSkillsUseCase softUseCase;
    private final SearchTechnicalSkillsUseCase searchTechnicalSkillsUseCase;
    private final GetSoftSkillUseCase softByIdUseCase;

    private final SkillsRestMapper mapper;

    @Operation(
            summary = "List language skills",
            description = """
            Returns a list of all available language skills.
            Each language skill represents a spoken language and
            includes basic proficiency information.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of language skills",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LanguageSkillResponse.class),
                                    examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 2354,
                                        "language": "Spanish"
                                      },
                                      {
                                        "id": 8161,
                                        "language": "English"
                                      }
                                    ]
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
            }
    )
    @GetMapping("/languages")
    public ResponseEntity<List<LanguageSkillResponse>> getLanguageSkills() {
        return ResponseEntity.ok(mapper.toLanguageResponseList(languagesUseCase.execute()));
    }

    @Operation(
            summary = "Get a language skill by id",
            description = """
            Returns the detailed information of a specific language skill
            identified by its numeric id.
            If the language skill does not exist, a 404 response is returned.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Language skill found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LanguageSkillDetailResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                        "id": 2354,
                                        "language": "Spanish",
                                        "listening": "Native",
                                        "reading": "Native",
                                        "spoken_production": "Native",
                                        "spoken_interaction": "Native",
                                        "writing": "Native"
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Language skill not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                    {
                                      "error": "Language skill not found."
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
            }
    )
    @GetMapping("/languages/{id}")
    public ResponseEntity<LanguageSkillDetailResponse> getLanguageSkillById(
            @Parameter(
                    description = "4-digit numeric id of the education entry",
                    example = "1234"
            )
            @PathVariable("id") int id
    ) {
        return languageByIdUseCase.execute(id)
                .map(mapper::toDetailResponse)
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
    public ResponseEntity<List<TechnicalSkillDetailResponse>> getTechnicalSkills(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "category", required = false) String category
    ) {
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria(name, category);

        List<TechnicalSkillDetailResponse> result =
                mapper.toTechnicalDetailResponseList(
                        searchTechnicalSkillsUseCase.execute(criteria)
                );

        return result.isEmpty()
                ? ResponseEntity.status(404).body(result)
                : ResponseEntity.ok(result);
    }

    @Operation(
            summary = "List soft skills",
            description = """
            Returns a list of all available soft skills.
            Soft skills represent personal and interpersonal abilities
            such as communication, teamwork, or problem-solving.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of soft skills",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SoftSkillResponse.class),
                                    examples = @ExampleObject(value = """
                                    [
                                      { "id": 1001, "name": "Communication" },
                                      { "id": 1002, "name": "Teamwork" }
                                    ]
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
            }
    )
    @GetMapping("/soft")
    public ResponseEntity<List<SoftSkillResponse>> getSoftSkills() {
        return ResponseEntity.ok(mapper.toSoftResponseList(softUseCase.execute()));
    }

    @Operation(
            summary = "Get a soft skill by id",
            description = """
            Returns the details of a specific soft skill identified by its id.
            If no soft skill exists with the provided id, a 404 response is returned.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Soft skill found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SoftSkillResponse.class),
                                    examples = @ExampleObject(value = """
                                    { "id": 1001, "name": "Communication" }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Soft skill not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                    {
                                      "error": "Soft skill not found."
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
            }
    )
    @GetMapping("/soft/{id}")
    public ResponseEntity<SoftSkillResponse> getSoftSkillById(
            @Parameter(
                    description = "4-digit numeric id of the soft skill",
                    example = "1234"
            )
            @PathVariable("id") int id
    ) {
        return softByIdUseCase.execute(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
