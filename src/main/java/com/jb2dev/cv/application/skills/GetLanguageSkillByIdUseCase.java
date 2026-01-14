package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;

import java.util.Optional;

@FunctionalInterface
public interface GetLanguageSkillByIdUseCase {
  Optional<LanguageSkill> execute(int id);
}
