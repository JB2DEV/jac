package com.jb2dev.cv.domain.profile.model;

public record ContactInfo(
    String address,
    String email,
    String mobilePhone,
    String landlinePhone,
    String linkedinUrl,
    String githubUrl
) {}
