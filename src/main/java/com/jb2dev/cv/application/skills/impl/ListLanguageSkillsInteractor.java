package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.ListLanguageSkillsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListLanguageSkillsInteractor implements ListLanguageSkillsUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public List<LanguageSkill> execute(Language language) {
    return skillsRepository.findAllLanguages(language);
  }
}
