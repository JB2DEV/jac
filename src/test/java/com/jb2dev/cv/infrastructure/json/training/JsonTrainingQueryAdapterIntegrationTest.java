package com.jb2dev.cv.infrastructure.json.training;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonTrainingQueryAdapterIntegrationTest {

    @Autowired
    private JsonTrainingQueryAdapter adapter;

    @Test
    void shouldFindAllTrainingsInEnglish() {
        List<TrainingItem> result = adapter.findAllTrainings(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
        assertThat(result).allMatch(item -> item.title() != null);
        assertThat(result).allMatch(item -> item.provider() != null);
        assertThat(result).allMatch(item -> item.credentialId() != null);
        assertThat(result).allMatch(item -> item.issuedDate() != null);
    }

    @Test
    void shouldFindAllTrainingsInSpanish() {
        List<TrainingItem> result = adapter.findAllTrainings(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.id() > 0);
    }

    @Test
    void shouldFindTrainingByCredentialId() {
        List<TrainingItem> all = adapter.findAllTrainings(Language.EN_EN);
        String existingCredentialId = all.getFirst().credentialId();

        Optional<TrainingItem> result = adapter.findTrainingById(existingCredentialId, Language.EN_EN);

        assertThat(result).isPresent();
        assertThat(result.get().credentialId()).isEqualTo(existingCredentialId);
        assertThat(result.get().title()).isNotNull();
        assertThat(result.get().provider()).isNotNull();
        assertThat(result.get().location()).isNotNull();
        assertThat(result.get().issuedDate()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenTrainingNotFound() {
        Optional<TrainingItem> result = adapter.findTrainingById("NON-EXISTENT-ID", Language.EN_EN);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSameNumberOfTrainingsForBothLanguages() {
        List<TrainingItem> english = adapter.findAllTrainings(Language.EN_EN);
        List<TrainingItem> spanish = adapter.findAllTrainings(Language.ES_ES);

        assertThat(english).hasSameSizeAs(spanish);
    }

    @Test
    void shouldValidateCredentialIdIsUnique() {
        List<TrainingItem> result = adapter.findAllTrainings(Language.EN_EN);

        assertThat(result).isNotEmpty();
        long uniqueCredentialIds = result.stream()
                .map(TrainingItem::credentialId)
                .distinct()
                .count();
        assertThat(uniqueCredentialIds).isEqualTo(result.size());
    }

    @Test
    void shouldValidateIssuedDateFormat() {
        List<TrainingItem> result = adapter.findAllTrainings(Language.EN_EN);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(item -> item.issuedDate() != null);
        assertThat(result).allMatch(item -> item.issuedDate().getYear() > 1900);
        assertThat(result).allMatch(item -> item.issuedDate().getMonthValue() >= 1 && item.issuedDate().getMonthValue() <= 12);
    }

    @Test
    void shouldIncludeOptionalFields() {
        List<TrainingItem> result = adapter.findAllTrainings(Language.EN_EN);

        assertThat(result).isNotEmpty();
        result.forEach(item -> {
            assertThat(item.credentialUrl()).isNotNull();
            assertThat(item.details()).isNotNull();
        });
    }
}
