package com.jb2dev.cv.application.experience;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;

import java.util.List;

@FunctionalInterface
public interface ListExperiencesUseCase {
  List<ExperienceItem> execute(Language language);
}
