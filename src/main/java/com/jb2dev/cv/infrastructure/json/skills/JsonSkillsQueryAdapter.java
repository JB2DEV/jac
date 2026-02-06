package com.jb2dev.cv.infrastructure.json.skills;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
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
    log.debug("Reading language skills from JSON file: {}", path);
    return reader.read(path, LANG_TYPE);
  }

  @Override
  public Optional<LanguageSkill> findLanguageById(int id, Language language) {
    log.debug("Searching language skill by id: {}", id);
    return findAllLanguages(language).stream().filter(s -> s.id() == id).findFirst();
  }

  @Override
  public List<TechnicalSkill> findAllTechnicalSkills() {
    String path = "data/commons/skills_technical.json";
    log.debug("Reading technical skills from JSON file: {}", path);
    return reader.read(path, TECH_TYPE);
  }

  @Override
  public List<TechnicalSkill> findTechnicalSkillByCategory(String category) {
      log.debug("Searching technical skills by category: {}", category);
      String needle = category.toLowerCase();
      List<TechnicalSkill> results = findAllTechnicalSkills().stream()
              .filter(skill -> skill.category().getLabel().toLowerCase().equals(needle)
                      || skill.category().name().toLowerCase().equals(needle))
              .toList();
      log.debug("Found {} technical skills for category: {}", results.size(), category);
      return results;
  }

  @Override
  public List<TechnicalSkill> findTechnicalSkillByName(String name) {
      log.debug("Searching technical skills by name: {}", name);
      String needle = name.toLowerCase();
      List<TechnicalSkill> results = findAllTechnicalSkills().stream()
              .filter(skill -> skill.name().toLowerCase().contains(needle))
              .toList();
      log.debug("Found {} technical skills for name: {}", results.size(), name);
      return results;
  }

  @Override
  public List<SoftSkill> findAllSoftSkills(Language language) {
    String path = language == Language.EN_EN ? "data/en/skills_soft.json" : "data/es/skills_soft.json";
    log.debug("Reading soft skills from JSON file: {}", path);
    return reader.read(path, SOFT_TYPE);
  }

  @Override
  public Optional<SoftSkill> findSoftSkillById(int id, Language language) {
    log.debug("Searching soft skill by id: {}", id);
    return findAllSoftSkills(language).stream().filter(s -> s.id() == id).findFirst();
  }
}
