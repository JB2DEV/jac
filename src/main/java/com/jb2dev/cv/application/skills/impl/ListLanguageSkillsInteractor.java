package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.ListLanguageSkillsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListLanguageSkillsInteractor implements ListLanguageSkillsUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public List<LanguageSkill> execute(Language language) {
    log.info("Executing ListLanguageSkills use case for language: {}", language);
    List<LanguageSkill> result = skillsRepository.findAllLanguages(language);
    log.debug("Retrieved {} language skills", result.size());
    return result;
  }
}
