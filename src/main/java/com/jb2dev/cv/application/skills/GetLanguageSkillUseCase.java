package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;


@FunctionalInterface
public interface GetLanguageSkillUseCase {
  LanguageSkill execute(int id, Language language);
}
