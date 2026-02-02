package com.jb2dev.cv.application.experience;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;

import java.util.Optional;

@FunctionalInterface
public interface GetExperienceUseCase {
  Optional<ExperienceItem> execute(int id, Language language);
}
