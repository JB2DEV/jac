package com.jb2dev.cv.infrastructure.rest.controllers.profile;

import com.jb2dev.cv.application.profile.GetContactInfoUseCase;
import com.jb2dev.cv.application.profile.GetPersonalInfoUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.infrastructure.rest.dto.profile.ContactInfoResponse;
import com.jb2dev.cv.infrastructure.rest.dto.profile.PersonalInfoResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.profile.ProfileRestMapper;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Profile", description = "Core personal and contact profile information.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProfileController {

  private final GetPersonalInfoUseCase personalInfoUseCase;
  private final GetContactInfoUseCase contactInfoUseCase;
  private final ProfileRestMapper mapper;

    @Operation(
            summary = "Get personal information",
            description = """
            Returns the core personal information of the profile.
            This endpoint provides general identity data such as
            full name, date of birth, nationality, and gender.
            """,
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Personal information successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PersonalInfoResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "full_name": "John Doe",
                                      "birth_date": "1990-01-15",
                                      "nationality": "American",
                                      "gender": "Male"
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
                                      "message": "Invalid language code: 'invalid'. Supported codes are: es_ES, en_EN",
                                      "path": "/api/v1/personal"
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
                                      "path": "/api/v1/personal"
                                    }
                                    """)
                            )
                    )
            }
    )
  @GetMapping("/personal")
  public ResponseEntity<PersonalInfoResponse> getPersonalInfo(@RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale) {
    Language language = Language.fromCode(locale);
    return ResponseEntity.ok(mapper.toResponse(personalInfoUseCase.execute(language)));
  }

    @Operation(
            summary = "Get contact information",
            description = """
            Returns the contact details associated with the profile.
            This includes physical address, email, phone numbers,
            and relevant online profiles such as LinkedIn or GitHub.
            """,
            parameters = {
                @Parameter(name = "Accept-Language", in = ParameterIn.HEADER, description = "Response language: es_ES for Spanish, en_EN for English", example = "es_ES", required = false)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contact information successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ContactInfoResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "address": "New York, USA",
                                      "email": "john.doe@example.com",
                                      "mobile_phone": "+1 555 123 4567",
                                      "website_url": "https://johndoe.dev",
                                      "linkedin_url": "https://linkedin.com/in/johndoe",
                                      "github_url": "https://github.com/johndoe"
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
                                      "message": "Invalid language code: 'pt_BR'. Supported codes are: es_ES, en_EN",
                                      "path": "/api/v1/contact"
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
                                      "path": "/api/v1/contact"
                                    }
                                    """)
                            )
                    )
            }
    )
  @GetMapping("/contact")
  public ResponseEntity<ContactInfoResponse> getContactInfo(@RequestHeader(value = "Accept-Language", defaultValue = "es_ES") String locale) {
    Language language = Language.fromCode(locale);
    return ResponseEntity.ok(mapper.toResponse(contactInfoUseCase.execute(language)));
  }
}
