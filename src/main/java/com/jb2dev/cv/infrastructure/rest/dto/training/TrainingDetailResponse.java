package com.jb2dev.cv.infrastructure.rest.dto.training;

import java.time.YearMonth;

public record TrainingDetailResponse(
        String title,
        String provider,
        String location,
        YearMonth issuedDate,
        String credentialId,
        String credentialUrl,
        String details
) {}
