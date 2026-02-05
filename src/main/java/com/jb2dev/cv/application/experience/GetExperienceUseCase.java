package com.jb2dev.cv.application.experience;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;


@FunctionalInterface
public interface GetExperienceUseCase {
  ExperienceItem execute(int id, Language language);
}
