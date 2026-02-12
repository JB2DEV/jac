package com.jb2dev.cv.domain.education.model;

import java.time.LocalDate;

public record EducationItem(
    int id,
    String title,
    String institution,
    String location,
    LocalDate startDate,
    LocalDate endDate,
    String details
) {}
