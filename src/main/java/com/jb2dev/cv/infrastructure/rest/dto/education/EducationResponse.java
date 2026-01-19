package com.jb2dev.cv.infrastructure.rest.dto.education;

import java.time.LocalDate;

public record EducationResponse(
    int id,
    String title,
    String institution,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    String details
) {}
