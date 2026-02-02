package com.jb2dev.cv.infrastructure.json.skills;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonSkillsQueryAdapter implements SkillsRepository {

  private static final TypeReference<List<LanguageSkill>> LANG_TYPE = new TypeReference<>() {};
  private static final TypeReference<List<TechnicalSkill>> TECH_TYPE = new TypeReference<>() {};
  private static final TypeReference<List<SoftSkill>> SOFT_TYPE = new TypeReference<>() {};

  private final ClasspathJsonReader reader;

  @Override
  public List<LanguageSkill> findAllLanguages(Language language) {
    String path = language == Language.EN_EN ? "data/en/skills_languages.json" : "data/es/skills_languages.json";
    return reader.read(path, LANG_TYPE);
  }

  @Override
  public Optional<LanguageSkill> findLanguageById(int id, Language language) {
    return findAllLanguages(language).stream().filter(s -> s.id() == id).findFirst();
  }

  @Override
  public List<TechnicalSkill> findAllTechnicalSkills() {
    String path = "data/commons/skills_technical.json";
    return reader.read(path, TECH_TYPE);
  }

  @Override
  public List<TechnicalSkill> findTechnicalSkillByCategory(String category) {
      String needle = category.toLowerCase();
      return findAllTechnicalSkills().stream()
              .filter(skill -> skill.category().getLabel().toLowerCase().equals(needle)
                      || skill.category().name().toLowerCase().equals(needle))
              .toList();
  }

  @Override
  public List<TechnicalSkill> findTechnicalSkillByName(String name) {
      String needle = name.toLowerCase();
      return findAllTechnicalSkills().stream()
              .filter(skill -> skill.name().toLowerCase().contains(needle))
              .toList();
  }

  @Override
  public List<SoftSkill> findAllSoftSkills(Language language) {
    String path = language == Language.EN_EN ? "data/en/skills_soft.json" : "data/es/skills_soft.json";
    return reader.read(path, SOFT_TYPE);
  }

  @Override
  public Optional<SoftSkill> findSoftSkillById(int id, Language language) {
    return findAllSoftSkills(language).stream().filter(s -> s.id() == id).findFirst();
  }
}
