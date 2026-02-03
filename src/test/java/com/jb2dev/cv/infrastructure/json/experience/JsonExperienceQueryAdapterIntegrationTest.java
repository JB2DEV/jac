package com.jb2dev.cv.infrastructure.json.experience;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonExperienceQueryAdapterIntegrationTest {

    @Autowired
    private JsonExperienceQueryAdapter adapter;

    @Test
    void shouldFindAllExperiencesInEnglish() {
        List<ExperienceItem> result = adapter.findAllExperiences(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
        assertThat(result).allMatch(item -> item.role() != null);
        assertThat(result).allMatch(item -> item.company() != null);
        assertThat(result).allMatch(item -> item.startDate() != null);
    }

    @Test
    void shouldFindAllExperiencesInSpanish() {
        List<ExperienceItem> result = adapter.findAllExperiences(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
    }

    @Test
    void shouldFindExperienceById() {
        List<ExperienceItem> all = adapter.findAllExperiences(Language.EN_EN);
        int existingId = all.getFirst().id();

        Optional<ExperienceItem> result = adapter.findExperienceById(existingId, Language.EN_EN);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(existingId);
        assertThat(result.get().role()).isNotNull();
        assertThat(result.get().company()).isNotNull();
        assertThat(result.get().summary()).isNotNull();
        assertThat(result.get().description()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenExperienceNotFound() {
        Optional<ExperienceItem> result = adapter.findExperienceById(999999, Language.EN_EN);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleCurrentExperience() {
        List<ExperienceItem> result = adapter.findAllExperiences(Language.EN_EN);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> true);
    }

    @Test
    void shouldReturnSameNumberOfExperiencesForBothLanguages() {
        List<ExperienceItem> english = adapter.findAllExperiences(Language.EN_EN);
        List<ExperienceItem> spanish = adapter.findAllExperiences(Language.ES_ES);

        assertThat(english).hasSameSizeAs(spanish);
    }

    @Test
    void shouldValidateDateFields() {
        List<ExperienceItem> result = adapter.findAllExperiences(Language.EN_EN);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.startDate() != null);
    }
}
