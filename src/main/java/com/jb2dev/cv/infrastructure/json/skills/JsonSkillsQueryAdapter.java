package com.jb2dev.cv.infrastructure.json.skills;

import com.fasterxml.jackson.core.type.TypeReference;
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
  public List<LanguageSkill> findAllLanguages() {
    return reader.read("data/skills_languages.json", LANG_TYPE);
  }

  @Override
  public Optional<LanguageSkill> findLanguageById(int id) {
    return findAllLanguages().stream().filter(s -> s.id() == id).findFirst();
  }

  @Override
  public List<TechnicalSkill> findAllTechnicalSkills() {
    return reader.read("data/skills_technical.json", TECH_TYPE);
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
  public List<SoftSkill> findAllSoftSkills() {
    return reader.read("data/skills_soft.json", SOFT_TYPE);
  }

  @Override
  public Optional<SoftSkill> findSoftSkillById(int id) {
    return findAllSoftSkills().stream().filter(s -> s.id() == id).findFirst();
  }
}
