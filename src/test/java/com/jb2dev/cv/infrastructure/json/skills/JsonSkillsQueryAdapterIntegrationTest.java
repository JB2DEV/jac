package com.jb2dev.cv.infrastructure.json.skills;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonSkillsQueryAdapterIntegrationTest {

    @Autowired
    private JsonSkillsQueryAdapter adapter;

    @Test
    void shouldFindAllLanguagesInEnglish() {
        List<LanguageSkill> result = adapter.findAllLanguages(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.id() > 0);
        assertThat(result).allMatch(skill -> skill.language() != null);
    }

    @Test
    void shouldFindAllLanguagesInSpanish() {
        List<LanguageSkill> result = adapter.findAllLanguages(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.id() > 0);
    }

    @Test
    void shouldFindLanguageById() {
        List<LanguageSkill> all = adapter.findAllLanguages(Language.EN_EN);
        int existingId = all.getFirst().id();

        Optional<LanguageSkill> result = adapter.findLanguageById(existingId, Language.EN_EN);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(existingId);
    }

    @Test
    void shouldReturnEmptyWhenLanguageNotFound() {
        Optional<LanguageSkill> result = adapter.findLanguageById(999999, Language.EN_EN);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllTechnicalSkills() {
        List<TechnicalSkill> result = adapter.findAllTechnicalSkills();

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.id() > 0);
        assertThat(result).allMatch(skill -> skill.name() != null);
        assertThat(result).allMatch(skill -> skill.category() != null);
    }

    @Test
    void shouldFindTechnicalSkillByCategory() {
        List<TechnicalSkill> result = adapter.findTechnicalSkillByCategory("Language");

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill ->
            skill.category().getLabel().equalsIgnoreCase("Language") ||
            skill.category().name().equalsIgnoreCase("Language")
        );
    }

    @Test
    void shouldFindTechnicalSkillByCategoryIgnoreCase() {
        List<TechnicalSkill> result1 = adapter.findTechnicalSkillByCategory("language");
        List<TechnicalSkill> result2 = adapter.findTechnicalSkillByCategory("LANGUAGE");

        assertThat(result1).isNotEmpty();
        assertThat(result2).isNotEmpty();
        assertThat(result1).hasSameSizeAs(result2);
    }

    @Test
    void shouldReturnEmptyListWhenCategoryNotFound() {
        List<TechnicalSkill> result = adapter.findTechnicalSkillByCategory("NonExistentCategory");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindTechnicalSkillByName() {
        List<TechnicalSkill> result = adapter.findTechnicalSkillByName("Java");

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.name().toLowerCase().contains("java"));
    }

    @Test
    void shouldFindTechnicalSkillByNamePartialMatch() {
        List<TechnicalSkill> result = adapter.findTechnicalSkillByName("Spr");

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.name().toLowerCase().contains("spr"));
    }

    @Test
    void shouldFindTechnicalSkillByNameIgnoreCase() {
        List<TechnicalSkill> result1 = adapter.findTechnicalSkillByName("java");
        List<TechnicalSkill> result2 = adapter.findTechnicalSkillByName("JAVA");

        assertThat(result1).isNotEmpty();
        assertThat(result2).isNotEmpty();
        assertThat(result1).hasSameSizeAs(result2);
    }

    @Test
    void shouldReturnEmptyListWhenNameNotFound() {
        List<TechnicalSkill> result = adapter.findTechnicalSkillByName("NonExistentSkill12345");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllSoftSkillsInEnglish() {
        List<SoftSkill> result = adapter.findAllSoftSkills(Language.EN_EN);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.id() > 0);
        assertThat(result).allMatch(skill -> skill.name() != null);
    }

    @Test
    void shouldFindAllSoftSkillsInSpanish() {
        List<SoftSkill> result = adapter.findAllSoftSkills(Language.ES_ES);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(skill -> skill.id() > 0);
    }

    @Test
    void shouldFindSoftSkillById() {
        List<SoftSkill> all = adapter.findAllSoftSkills(Language.EN_EN);
        int existingId = all.getFirst().id();

        Optional<SoftSkill> result = adapter.findSoftSkillById(existingId, Language.EN_EN);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(existingId);
    }

    @Test
    void shouldReturnEmptyWhenSoftSkillNotFound() {
        Optional<SoftSkill> result = adapter.findSoftSkillById(999999, Language.EN_EN);

        assertThat(result).isEmpty();
    }
}
