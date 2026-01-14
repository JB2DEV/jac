package com.jb2dev.cv.infrastructure.rest.dto.training;

import java.time.LocalDate;

public record TrainingResponse(
    int id,
    String title,
    String provider,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    String details
) {}
