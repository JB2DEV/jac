package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.GetSoftSkillUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetSoftSkillInteractor implements GetSoftSkillUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public SoftSkill execute(int id, Language language) {
    log.info("Executing GetSoftSkill use case: id={}, language={}", id, language);

    SoftSkill result = skillsRepository.findSoftSkillById(id, language)
        .orElseThrow(() -> {
          log.warn("SoftSkill not found: id={}, language={}", id, language);
          return new ResourceNotFoundException("SoftSkill", String.valueOf(id));
        });

    log.debug("SoftSkill retrieved: id={}, name={}", result.id(), result.name());
    return result;
  }
}
