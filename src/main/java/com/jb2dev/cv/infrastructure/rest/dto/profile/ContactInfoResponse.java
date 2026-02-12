package com.jb2dev.cv.infrastructure.rest.dto.profile;

public record ContactInfoResponse(
    String address,
    String email,
    String mobilePhone,
    String linkedinUrl,
    String githubUrl
) {}
