package com.jb2dev.cv.infrastructure.rest.controllers.experience;

import com.jb2dev.cv.application.experience.GetExperienceUseCase;
import com.jb2dev.cv.application.experience.ListExperiencesUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceDetailResponse;
import com.jb2dev.cv.infrastructure.rest.dto.experience.ExperienceResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.experience.ExperienceRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
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
            description = "Returns a list of professional experience entries. Each entry contains a short summary of a role or position.Results are ordered by start date in descending order.",
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
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
                            responseCode = "400",
                            description = "Bad Request - Invalid language code",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid language code: 'de_DE'. Supported codes are: es_ES, en_EN",
                                              "path": "/api/v1/experience"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/api/v1/experience"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getExperiences(@RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale) {
        Language language = Language.fromCode(locale);
        return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute(language)));
    }

    @Operation(
            summary = "Get an experience entry by id",
            description = """
                    Returns the detailed information of a specific experience entry
                    identified by its numeric id.
                    """,
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
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
                                              "start_date": "2021-01-01",
                                              "end_date": "2024-06-30",
                                              "current": false,
                                              "summary": "Backend development with Java and Spring Boot",
                                              "description": "Design and implementation of scalable backend services using Spring Boot, JPA, and PostgreSQL."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid language code",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Invalid language code: 'de_DE'. Supported codes are: es_ES, en_EN",
                                              "path": "/api/v1/experience/6541"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Experience entry does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Experience with id '9999' not found",
                                              "path": "/api/v1/experience/9999"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred while processing your request",
                                              "path": "/api/v1/experience/6541"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDetailResponse> getExperienceById(
            @Parameter(description = "4-digit numeric id of the experience entry", example = "1234")
            @PathVariable("id") int id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale
    ) {
        Language language = Language.fromCode(locale);
        ExperienceItem experience = getByIdUseCase.execute(id, language);
        return ResponseEntity.ok(mapper.toDetailResponse(experience));
    }
}
