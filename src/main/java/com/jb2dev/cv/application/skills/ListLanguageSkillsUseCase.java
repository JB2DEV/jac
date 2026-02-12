package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;

import java.util.List;

@FunctionalInterface
public interface ListLanguageSkillsUseCase {
  List<LanguageSkill> execute(Language language);
}
