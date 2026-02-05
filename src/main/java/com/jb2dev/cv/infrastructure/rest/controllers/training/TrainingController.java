package com.jb2dev.cv.infrastructure.rest.controllers.training;

import com.jb2dev.cv.application.training.GetTrainingUseCase;
import com.jb2dev.cv.application.training.ListTrainingsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingDetailResponse;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.training.TrainingRestMapper;
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

@Tag(name = "Training", description = "Non-formal training entries (courses, workshops, learning paths).")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    private final ListTrainingsUseCase listUseCase;
    private final GetTrainingUseCase getByIdUseCase;
    private final TrainingRestMapper mapper;

    @Operation(
            summary = "List all training entries",
            description = "Returns a list of non-formal training entries including courses, workshops, and learning paths. " +
                    "The list is sorted by issuance date in descending order.",
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of training entries",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingResponse.class),
                                    examples = @ExampleObject(value = """
                        [
                          {
                            "title": "Backend Development with Spring Boot",
                            "credential_id": "EUJB"
                          },
                          {
                            "title": "Security and Authentication in Spring",
                            "credential_id": "N67H"
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
                          "message": "Invalid language code: 'ru_RU'. Supported codes are: es_ES, en_EN",
                          "path": "/api/v1/training"
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
                          "path": "/api/v1/training"
                        }
                    """)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<TrainingResponse>> getTrainings(@RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale) {
        Language language = Language.fromCode(locale);
        return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute(language)));
    }

    @Operation(
            summary = "Get a training entry by credential ID",
            description = "Retrieves detailed information of a training entry given its unique credential ID." ,
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Training entry found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TrainingDetailResponse.class),
                                    examples = @ExampleObject(value = """
                        {
                          "title": "Backend Development with Spring Boot",
                          "provider": "OpenWebinars",
                          "location": "Spain (online)",
                          "issued_date": "2025-03",
                          "credential_id": "EUJB",
                          "credential_url": "https://openwebinars.net/cert/EUJB",
                          "details": "Specialization in backend development with DevOps hands-on practice."
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
                          "message": "Invalid language code: 'ru_RU'. Supported codes are: es_ES, en_EN",
                          "path": "/api/v1/training/EUJB"
                        }
                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Training entry does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                        {
                          "timestamp": "2026-02-05T12:30:00",
                          "status": 404,
                          "error": "Not Found",
                          "message": "Training with id 'NOTFOUND' not found",
                          "path": "/api/v1/training/NOTFOUND"
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
                          "path": "/api/v1/training/EUJB"
                        }
                    """)
                            )
                    )
            }
    )
    @GetMapping("/{credentialId}")
    public ResponseEntity<TrainingDetailResponse> getTrainingById(
            @Parameter(
                    description = "Unique credential ID of the training entry",
                    required = true,
                    example = "EUJB"
            )
            @PathVariable("credentialId") String credentialId,
            @RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale
    ) {
        Language language = Language.fromCode(locale);
        TrainingItem training = getByIdUseCase.execute(credentialId, language);
        return ResponseEntity.ok(mapper.toResponse(training));
    }
}
