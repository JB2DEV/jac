package com.jb2dev.cv.domain.profile.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactInfoTest {

    @Test
    void shouldCreateContactInfo() {
        // When
        ContactInfo info = new ContactInfo(
                "123 Main St",
                "john@example.com",
                "+1234567890",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );

        // Then
        assertThat(info.address()).isEqualTo("123 Main St");
        assertThat(info.email()).isEqualTo("john@example.com");
        assertThat(info.mobilePhone()).isEqualTo("+1234567890");
        assertThat(info.linkedinUrl()).isEqualTo("https://linkedin.com/in/johndoe");
        assertThat(info.githubUrl()).isEqualTo("https://github.com/johndoe");
    }

    @Test
    void shouldCompareEqualContactInfo() {
        // Given
        ContactInfo info1 = new ContactInfo(
                "123 Main St",
                "john@example.com",
                "+1234567890",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );
        ContactInfo info2 = new ContactInfo(
                "123 Main St",
                "john@example.com",
                "+1234567890",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );

        // Then
        assertThat(info1).isEqualTo(info2);
        assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
    }

    @Test
    void shouldCompareDifferentContactInfo() {
        // Given
        ContactInfo info1 = new ContactInfo(
                "123 Main St",
                "john@example.com",
                "+1234567890",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );
        ContactInfo info2 = new ContactInfo(
                "456 Oak Ave",
                "jane@example.com",
                "+9876543210",
                "https://linkedin.com/in/janesmith",
                "https://github.com/janesmith"
        );

        // Then
        assertThat(info1).isNotEqualTo(info2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        ContactInfo info = new ContactInfo(
                "123 Main St",
                "john@example.com",
                "+1234567890",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );

        // When
        String result = info.toString();

        // Then
        assertThat(result).contains("john@example.com");
        assertThat(result).contains("123 Main St");
    }
}
