package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;

@FunctionalInterface
public interface GetSoftSkillUseCase {
  SoftSkill execute(int id, Language language);
}
