package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.ListSoftSkillsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListSoftSkillsInteractor implements ListSoftSkillsUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public List<SoftSkill> execute(Language language) {
    log.info("Executing ListSoftSkills use case for language: {}", language);
    List<SoftSkill> result = skillsRepository.findAllSoftSkills(language);
    log.debug("Retrieved {} soft skills", result.size());
    return result;
  }
}
