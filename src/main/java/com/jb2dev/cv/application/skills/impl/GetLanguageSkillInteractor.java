package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.GetLanguageSkillUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetLanguageSkillInteractor implements GetLanguageSkillUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public LanguageSkill execute(int id, Language language) {
    log.info("Executing GetLanguageSkill use case: id={}, language={}", id, language);

    LanguageSkill result = skillsRepository.findLanguageById(id, language)
        .orElseThrow(() -> {
          log.warn("LanguageSkill not found: id={}, language={}", id, language);
          return new ResourceNotFoundException("LanguageSkill", String.valueOf(id));
        });

    log.debug("LanguageSkill retrieved: id={}, language={}, listening={}, reading={}, spokenProduction={}, spokenInteraction={}, writing={}",
        result.id(), result.language(), result.listening(), result.reading(), result.spokenProduction(), result.spokenInteraction(), result.writing());
    return result;
  }
}
