package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.Optional;

@FunctionalInterface
public interface GetTechnicalSkillByIdUseCase {
  Optional<TechnicalSkill> execute(int id);
}
