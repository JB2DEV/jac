package com.jb2dev.cv.domain.training.model;

import java.time.YearMonth;

public record TrainingItem(
    int id,
    String title,
    String provider,
    String location,
    YearMonth issuedDate,
    String credentialId,
    String credentialUrl,
    String details
) {}
