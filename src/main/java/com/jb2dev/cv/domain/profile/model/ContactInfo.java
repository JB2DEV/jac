package com.jb2dev.cv.domain.profile.model;

/**
 * Domain model placeholder (1.1.0).
 * This will be secured in later releases (API Key).
 */
public record ContactInfo(
    String address,
    String email,
    String mobilePhone,
    String landlinePhone,
    String websiteUrl,
    String linkedinUrl,
    String githubUrl
) {}
