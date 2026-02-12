package com.jb2dev.cv.infrastructure.json.profile;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonContactInfoQueryAdapterIntegrationTest {

    @Autowired
    private JsonContactInfoQueryAdapter adapter;

    @Test
    void shouldGetContactInfoInEnglish() {
        ContactInfo result = adapter.getContactInfo(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result.address()).isNotNull();
        assertThat(result.email()).isNotNull();
        assertThat(result.mobilePhone()).isNotNull();
        assertThat(result.linkedinUrl()).isNotNull();
        assertThat(result.githubUrl()).isNotNull();
    }

    @Test
    void shouldGetContactInfoInSpanish() {
        ContactInfo result = adapter.getContactInfo(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result.address()).isNotNull();
        assertThat(result.email()).isNotNull();
        assertThat(result.mobilePhone()).isNotNull();
    }

    @Test
    void shouldReturnSameEmailForBothLanguages() {
        ContactInfo english = adapter.getContactInfo(Language.EN_EN);
        ContactInfo spanish = adapter.getContactInfo(Language.ES_ES);

        assertThat(english.email()).isEqualTo(spanish.email());
    }

    @Test
    void shouldReturnValidEmailFormat() {
        ContactInfo result = adapter.getContactInfo(Language.EN_EN);

        assertThat(result.email()).matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
