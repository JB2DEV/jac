package com.jb2dev.cv.infrastructure.json.education;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonEducationQueryAdapterIntegrationTest {

    @Autowired
    private JsonEducationQueryAdapter adapter;

    @Test
    void shouldFindAllEducationsInEnglish() {
        List<EducationItem> result = adapter.findAllEducations(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
        assertThat(result).allMatch(item -> item.title() != null);
        assertThat(result).allMatch(item -> item.institution() != null);
        assertThat(result).allMatch(item -> item.startDate() != null);
        assertThat(result).allMatch(item -> item.endDate() != null);
    }

    @Test
    void shouldFindAllEducationsInSpanish() {
        List<EducationItem> result = adapter.findAllEducations(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
    }

    @Test
    void shouldFindEducationById() {
        List<EducationItem> all = adapter.findAllEducations(Language.EN_EN);
        int existingId = all.getFirst().id();

        Optional<EducationItem> result = adapter.findEducationById(existingId, Language.EN_EN);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(existingId);
        assertThat(result.get().title()).isNotNull();
        assertThat(result.get().institution()).isNotNull();
        assertThat(result.get().location()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenEducationNotFound() {
        Optional<EducationItem> result = adapter.findEducationById(999999, Language.EN_EN);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSameNumberOfEducationsForBothLanguages() {
        List<EducationItem> english = adapter.findAllEducations(Language.EN_EN);
        List<EducationItem> spanish = adapter.findAllEducations(Language.ES_ES);

        assertThat(english).hasSameSizeAs(spanish);
    }

    @Test
    void shouldValidateDateFieldsAreNotNull() {
        List<EducationItem> result = adapter.findAllEducations(Language.EN_EN);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.startDate() != null);
        assertThat(result).allMatch(item -> item.endDate() != null);
    }

    @Test
    void shouldValidateStartDateBeforeEndDate() {
        List<EducationItem> result = adapter.findAllEducations(Language.EN_EN);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item ->
            item.startDate().isBefore(item.endDate()) ||
            item.startDate().isEqual(item.endDate())
        );
    }
}
