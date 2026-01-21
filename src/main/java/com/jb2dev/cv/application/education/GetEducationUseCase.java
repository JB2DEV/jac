package com.jb2dev.cv.application.education;

import com.jb2dev.cv.domain.education.model.EducationItem;

import java.util.Optional;

@FunctionalInterface
public interface GetEducationUseCase {
  Optional<EducationItem> execute(int id);
}
