package com.jb2dev.cv.infrastructure.rest.controllers.profile;

import com.jb2dev.cv.application.profile.GetContactInfoUseCase;
import com.jb2dev.cv.application.profile.GetPersonalInfoUseCase;
import com.jb2dev.cv.infrastructure.rest.dto.profile.ContactInfoResponse;
import com.jb2dev.cv.infrastructure.rest.dto.profile.PersonalInfoResponse;
import com.jb2dev.cv.infrastructure.rest.mappers.profile.ProfileRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Profile", description = "Core profile information.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProfileController {

  private final GetPersonalInfoUseCase personalInfoUseCase;
  private final GetContactInfoUseCase contactInfoUseCase;
  private final ProfileRestMapper mapper;

  @Operation(summary = "Get personal information")
  @GetMapping("/personal")
  public ResponseEntity<PersonalInfoResponse> personal() {
    return ResponseEntity.ok(mapper.toResponse(personalInfoUseCase.execute()));
  }

  @Operation(summary = "Get contact information")
  @GetMapping("/contact")
  public ResponseEntity<ContactInfoResponse> contact() {
    return ResponseEntity.ok(mapper.toResponse(contactInfoUseCase.execute()));
  }
}
