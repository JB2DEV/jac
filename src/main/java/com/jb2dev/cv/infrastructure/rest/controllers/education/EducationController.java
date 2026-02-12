package com.jb2dev.cv.infrastructure.rest.controllers.education;

import com.jb2dev.cv.application.education.GetEducationUseCase;
import com.jb2dev.cv.application.education.ListEducationsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.infrastructure.rest.dto.education.EducationResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.education.EducationRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Education", description = "Formal education entries.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/education")
public class EducationController {

    private final ListEducationsUseCase listUseCase;
    private final GetEducationUseCase getByIdUseCase;
    private final EducationRestMapper mapper;

    @Operation(
            summary = "List formal education entries",
            description = "Returns a list of formal education records. Each entry represents an academic experience such as degrees, postgraduate studies, or other formal education.",
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of education entries",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EducationResponse.class),
                                    examples = @ExampleObject(value = """
                                            [
                                              {
                                                "id": 3012,
                                                "title": "Bachelor of Computer Science",
                                                "institution": "University of Madrid",
                                                "location": "Madrid, Spain",
                                                "start_date": "2008-09-01",
                                                "end_date": "2012-06-30",
                                                "details": "Focused on software engineering and distributed systems."
                                              },
                                              {
                                                "id": 4187,
                                                "title": "Master in Software Engineering",
                                                "institution": "Polytechnic University of Madrid",
                                                "location": "Madrid, Spain",
                                                "start_date": "2013-09-01",
                                                "end_date": "2014-06-30",
                                                "details": "Advanced studies in architecture, DevOps, and cloud computing."
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
                                              "message": "Invalid language code: 'fr_FR'. Supported codes are: es_ES, en_EN",
                                              "path": "/api/v1/education"
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
                                              "path": "/api/v1/education"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<EducationResponse>> getEducations(@RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale) {
        Language language = Language.fromCode(locale);
        return ResponseEntity.ok(mapper.toResponseList(listUseCase.execute(language)));
    }

    @Operation(
            summary = "Get a formal education entry by id",
            description = "Returns the details of a specific education entry identified by its numeric id.",
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Education entry found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EducationResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "id": 3012,
                                              "title": "Bachelor of Computer Science",
                                              "institution": "University of Madrid",
                                              "location": "Madrid, Spain",
                                              "start_date": "2008-09-01",
                                              "end_date": "2012-06-30",
                                              "details": "Focused on software engineering and distributed systems."
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
                                              "message": "Invalid language code: 'fr_FR'. Supported codes are: es_ES, en_EN",
                                              "path": "/api/v1/education/3012"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Education entry does not exist",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "timestamp": "2026-02-05T12:30:00",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Education with id '9999' not found",
                                              "path": "/api/v1/education/9999"
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
                                              "path": "/api/v1/education/3012"
                                            }
                                            """)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EducationResponse> getEducationById(
            @Parameter(description = "4-digit numeric id of the education entry", example = "1234")
            @PathVariable("id") int id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale
    ) {
        Language language = Language.fromCode(locale);
        EducationItem education = getByIdUseCase.execute(id, language);
        return ResponseEntity.ok(mapper.toResponse(education));
    }
}
