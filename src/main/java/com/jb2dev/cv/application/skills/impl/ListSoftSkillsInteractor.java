package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.ListSoftSkillsUseCase;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListSoftSkillsInteractor implements ListSoftSkillsUseCase {

  private final SkillsRepository skillsRepository;

  @Override
  public List<SoftSkill> execute() {
    return skillsRepository.findAllSoftSkills();
  }
}
