package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.GetSoftSkillUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetSoftSkillInteractor implements GetSoftSkillUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public SoftSkill execute(int id, Language language) {
    return skillsRepository.findSoftSkillById(id, language)
        .orElseThrow(() -> new ResourceNotFoundException("SoftSkill", String.valueOf(id)));
  }
}
