package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.GetLanguageSkillUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetLanguageSkillInteractor implements GetLanguageSkillUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public LanguageSkill execute(int id, Language language) {
    return skillsRepository.findLanguageById(id, language)
        .orElseThrow(() -> new ResourceNotFoundException("LanguageSkill", String.valueOf(id)));
  }
}
