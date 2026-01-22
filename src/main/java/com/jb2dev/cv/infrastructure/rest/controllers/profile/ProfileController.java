package com.jb2dev.cv.infrastructure.rest.controllers.profile;

import com.jb2dev.cv.application.profile.GetContactInfoUseCase;
import com.jb2dev.cv.application.profile.GetPersonalInfoUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.profile.ContactInfoResponse;
import com.jb2dev.cv.infrastructure.rest.dto.profile.PersonalInfoResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.profile.ProfileRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Personal information successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PersonalInfoResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "full_name": "Javier Álvarez Cáceres",
                                      "birth_date": "1990-05-21",
                                      "nationality": "Spanish",
                                      "gender": "Male"
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
  @GetMapping("/personal")
  public ResponseEntity<PersonalInfoResponse> getPersonalInfo() {
    return ResponseEntity.ok(mapper.toResponse(personalInfoUseCase.execute()));
  }

    @Operation(
            summary = "Get contact information",
            description = """
            Returns the contact details associated with the profile.
            This includes physical address, email, phone numbers,
            and relevant online profiles such as LinkedIn or GitHub.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Contact information successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ContactInfoResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "address": "Madrid, Spain",
                                      "email": "javier.alvarez@example.com",
                                      "mobile_phone": "+34 600 123 456",
                                      "landline_phone": "+34 91 123 45 67",
                                      "website_url": "https://jb2dev.com",
                                      "linkedin_url": "https://linkedin.com/in/jb2dev",
                                      "github_url": "https://github.com/jb2dev"
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
  @GetMapping("/contact")
  public ResponseEntity<ContactInfoResponse> getContactInfo() {
    return ResponseEntity.ok(mapper.toResponse(contactInfoUseCase.execute()));
  }
}
