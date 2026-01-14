package com.jb2dev.cv.domain.certifications.model;

import java.time.LocalDate;

public record CertificationItem(
    int id,
    String name,
    String issuer,
    String credentialId,
    String credentialUrl,
    LocalDate issueDate,
    LocalDate expirationDate,
    String details
) {}
