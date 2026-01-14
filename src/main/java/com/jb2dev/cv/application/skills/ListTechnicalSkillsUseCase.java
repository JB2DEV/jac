package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.List;

@FunctionalInterface
public interface ListTechnicalSkillsUseCase {
  List<TechnicalSkill> execute();
}
