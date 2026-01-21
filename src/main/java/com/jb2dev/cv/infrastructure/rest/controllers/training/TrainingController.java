package com.jb2dev.cv.infrastructure.rest.controllers.training;

import com.jb2dev.cv.application.training.GetTrainingUseCase;
import com.jb2dev.cv.application.training.ListTrainingsUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingDetailResponse;
import com.jb2dev.cv.infrastructure.rest.dto.training.TrainingResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.training.TrainingRestMapper;
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
                            "credentialId": "EUJB"
                          },
                          {
                            "title": "Security and Authentication in Spring",
                            "credentialId": "N67H"
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
    public ResponseEntity<List<TrainingResponse>> list() {
        return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute()));
    }

    @Operation(
            summary = "Get a training entry by credential ID",
            description = "Retrieves detailed information of a training entry given its unique credential ID. " +
                    "The credential ID is the identifier of the training.",
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
                          "location": "España (online)",
                          "issuedDate": "2025-03",
                          "credentialId": "EUJB",
                          "credentialUrl": "https://openwebinars.net/cert/EUJB",
                          "details": "Specialization in backend development with DevOps hands-on practice."
                        }
                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Training entry not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                        {
                          "error": "Training entry with credential ID 'XXXX' not found."
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
    @GetMapping("/{credentialId}")
    public ResponseEntity<TrainingDetailResponse> getById(
            @Parameter(
                    description = "Unique credential ID of the training entry",
                    required = true,
                    example = "EUJB"
            )
            @PathVariable("credentialId") String credentialId
    ) {
        return getByIdUseCase.execute(credentialId)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
