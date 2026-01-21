package com.jb2dev.cv.application.education;

import com.jb2dev.cv.domain.education.model.EducationItem;

import java.util.List;

@FunctionalInterface
public interface ListEducationsUseCase {
  List<EducationItem> execute();
}
