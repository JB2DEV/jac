package com.jb2dev.cv.infrastructure.rest.dto.profile;

public record ContactInfoResponse(
    String address,
    String email,
    String mobilePhone,
    String landlinePhone,
    String websiteUrl,
    String linkedinUrl,
    String githubUrl
) {}
