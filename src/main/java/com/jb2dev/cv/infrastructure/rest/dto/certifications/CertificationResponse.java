package com.jb2dev.cv.infrastructure.rest.dto.certifications;

import java.time.LocalDate;

public record CertificationResponse(
    int id,
    String name,
    String issuer,
    String credentialId,
    String credentialUrl,
    LocalDate issueDate,
    LocalDate expirationDate,
    String details
) {}
