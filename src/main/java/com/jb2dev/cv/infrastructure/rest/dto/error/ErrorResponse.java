package com.jb2dev.cv.infrastructure.rest.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(description = "Standard error response")
public record ErrorResponse(
    @Schema(description = "Timestamp when the error occurred", example = "2026-02-05T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime timestamp,

    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "Error type", example = "Bad Request")
    String error,

    @Schema(description = "Error message", example = "Invalid parameter value")
    String message,

    @Schema(description = "Request path", example = "/api/v1/contact")
    String path
) {}
