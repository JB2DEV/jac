package com.jb2dev.cv.application.education;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;


@FunctionalInterface
public interface GetEducationUseCase {
  EducationItem execute(int id, Language language);
}
