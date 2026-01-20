package com.jb2dev.cv.infrastructure.json.skills;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JsonSkillsQueryAdapter implements SkillsQueryPort {

  private static final TypeReference<List<LanguageSkill>> LANG_TYPE = new TypeReference<>() {};
  private static final TypeReference<List<TechnicalSkill>> TECH_TYPE = new TypeReference<>() {};
  private static final TypeReference<List<SoftSkill>> SOFT_TYPE = new TypeReference<>() {};

  private final ClasspathJsonReader reader;

  @Override
  public List<LanguageSkill> languages() {
    return reader.read("data/skills_languages.json", LANG_TYPE);
  }

  @Override
  public Optional<LanguageSkill> languageById(int id) {
    return languages().stream().filter(s -> s.id() == id).findFirst();
  }

  @Override
  public List<TechnicalSkill> technicalList() {
    return reader.read("data/skills_technical.json", TECH_TYPE);
  }

  @Override
  public List<TechnicalSkill> technicalByCategory(String category) {
      String needle = category.toLowerCase();

      return technicalList().stream()
              .filter(skill -> skill.category().getLabel().toLowerCase().equals(needle)
                      || skill.category().name().toLowerCase().equals(needle))
              .toList();
  }

  @Override
  public List<TechnicalSkill> technicalByName(String name) {
      String needle = name.toLowerCase();

      return technicalList().stream()
              .filter(skill -> skill.name().toLowerCase().contains(needle))
              .toList();
  }

  @Override
  public List<SoftSkill> soft() {
    return reader.read("data/skills_soft.json", SOFT_TYPE);
  }

  @Override
  public Optional<SoftSkill> softById(int id) {
    return soft().stream().filter(s -> s.id() == id).findFirst();
  }
}
