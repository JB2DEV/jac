package com.jb2dev.cv.domain.experience.model;

import java.time.LocalDate;

public record ExperienceItem(
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
