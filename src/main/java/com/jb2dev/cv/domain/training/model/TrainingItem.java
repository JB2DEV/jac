package com.jb2dev.cv.domain.training.model;

import java.time.LocalDate;

public record TrainingItem(
    int id,
    String title,
    String provider,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    String details
) {}
