package com.jb2dev.cv.infrastructure.rest.controllers.experience;

import com.jb2dev.cv.application.experience.GetExperienceUseCase;
import com.jb2dev.cv.application.experience.ListExperiencesUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceDetailResponse;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.experience.ExperienceRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Experience", description = "Professional work experience entries.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    private final ListExperiencesUseCase listUseCase;
    private final GetExperienceUseCase getByIdUseCase;
    private final ExperienceRestMapper mapper;

    @Operation(
            summary = "List experience entries",
            description = """
                    Returns a list of professional experience entries.
                    Each entry contains a short summary of a role or position.
                    Results are ordered by start date in descending order.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of experience entries",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExperienceResponse.class),
                                    examples = @ExampleObject(value = """
                                            [
                                              {
                                                "id": 101,
                                                "role": "Senior Software Engineer",
                                                "summary": "Backend development with Java and Spring Boot"
                                              },
                                              {
                                                "id": 102,
                                                "role": "Software Engineer",
                                                "summary": "Development of REST APIs and microservices"
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
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getExperiences() {
        return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
    }

    @Operation(
            summary = "Get an experience entry by id",
            description = """
                    Returns the detailed information of a specific experience entry
                    identified by its numeric id.
                    If the experience entry does not exist, a 404 response is returned.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Experience entry found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExperienceDetailResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "id": 101,
                                              "role": "Senior Software Engineer",
                                              "company": "Acme Corp",
                                              "location": "Remote",
                                              "startDate": "2021-01-01",
                                              "endDate": "2024-06-30",
                                              "current": false,
                                              "summary": "Backend development with Java and Spring Boot",
                                              "description": "Design and implementation of scalable backend services using Spring Boot, JPA, and PostgreSQL."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Experience entry not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "error": "Experience entry not found."
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
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDetailResponse> getExperienceById(
            @Parameter(
                    description = "4-digit numeric id of the experience entry",
                    example = "101"
            )
            @PathVariable("id") int id
    ) {
        return getByIdUseCase.execute(id)
                .map(mapper::toDetailResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
