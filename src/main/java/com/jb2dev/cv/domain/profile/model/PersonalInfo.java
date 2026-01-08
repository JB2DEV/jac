package com.jb2dev.cv.domain.profile.model;

import java.time.LocalDate;

/**
 * Domain model placeholder (1.1.0).
 * Concrete fields and invariants will be refined in later releases.
 */
public record PersonalInfo(
    String fullName,
    LocalDate birthDate,
    String nationality,
    String gender
) {}
