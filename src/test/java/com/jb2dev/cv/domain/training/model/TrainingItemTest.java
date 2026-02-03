package com.jb2dev.cv.domain.training.model;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingItemTest {

    @Test
    void shouldCreateTrainingItem() {
        // Given
        YearMonth issuedDate = YearMonth.of(2023, 6);

        // When
        TrainingItem item = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                issuedDate,
                "CERT-12345",
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );

        // Then
        assertThat(item.id()).isEqualTo(1);
        assertThat(item.title()).isEqualTo("AWS Certified Solutions Architect");
        assertThat(item.provider()).isEqualTo("Amazon Web Services");
        assertThat(item.location()).isEqualTo("Online");
        assertThat(item.issuedDate()).isEqualTo(issuedDate);
        assertThat(item.credentialId()).isEqualTo("CERT-12345");
        assertThat(item.credentialUrl()).isEqualTo("https://aws.amazon.com/certification/verify/12345");
        assertThat(item.details()).isEqualTo("Professional level certification");
    }

    @Test
    void shouldCompareEqualTrainingItems() {
        // Given
        YearMonth issuedDate = YearMonth.of(2023, 6);
        TrainingItem item1 = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                issuedDate,
                "CERT-12345",
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );
        TrainingItem item2 = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                issuedDate,
                "CERT-12345",
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );

        // Then
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldCompareDifferentTrainingItems() {
        // Given
        TrainingItem item1 = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                YearMonth.of(2023, 6),
                "CERT-12345",
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );
        TrainingItem item2 = new TrainingItem(
                2,
                "Azure Administrator",
                "Microsoft",
                "Online",
                YearMonth.of(2023, 8),
                "CERT-67890",
                "https://microsoft.com/certification/verify/67890",
                "Administrator certification"
        );

        // Then
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        TrainingItem item = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                YearMonth.of(2023, 6),
                "CERT-12345",
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );

        // When
        String result = item.toString();

        // Then
        assertThat(result).contains("AWS Certified Solutions Architect");
        assertThat(result).contains("CERT-12345");
    }
}
