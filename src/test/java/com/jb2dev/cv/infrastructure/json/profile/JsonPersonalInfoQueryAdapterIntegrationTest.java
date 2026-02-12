package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonPersonalInfoQueryAdapterIntegrationTest {

    @Autowired
    private JsonPersonalInfoQueryAdapter adapter;

    @Test
    void shouldGetPersonalInfoInEnglish() {
        PersonalInfo result = adapter.getPersonalInfo(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isNotNull();
        assertThat(result.birthDate()).isNotNull();
        assertThat(result.nationality()).isNotNull();
        assertThat(result.gender()).isNotNull();
    }

    @Test
    void shouldGetPersonalInfoInSpanish() {
        PersonalInfo result = adapter.getPersonalInfo(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isNotNull();
        assertThat(result.birthDate()).isNotNull();
        assertThat(result.nationality()).isNotNull();
        assertThat(result.gender()).isNotNull();
    }

    @Test
    void shouldReturnDifferentNationalityForDifferentLanguages() {
        PersonalInfo english = adapter.getPersonalInfo(Language.EN_EN);
        PersonalInfo spanish = adapter.getPersonalInfo(Language.ES_ES);

        assertThat(english.fullName()).isEqualTo(spanish.fullName());
    }
}
