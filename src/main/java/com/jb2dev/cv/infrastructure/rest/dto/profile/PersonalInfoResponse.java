package com.jb2dev.cv.infrastructure.rest.dto.profile;

public record PersonalInfoResponse(
    String fullName,
    String birthDate,
    String nationality,
    String gender
) {}
