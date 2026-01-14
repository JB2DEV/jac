package com.jb2dev.cv.infrastructure.rest.dto.experience;

import java.time.LocalDate;

public record ExperienceResponse(
    int id,
    String role,
    String company,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    boolean current,
    String summary,
    String description
) {}
