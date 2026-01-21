package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.GetSoftSkillUseCase;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetSoftSkillInteractor implements GetSoftSkillUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public Optional<SoftSkill> execute(int id) {
    return skillsRepository.findSoftSkillById(id);
  }
}
