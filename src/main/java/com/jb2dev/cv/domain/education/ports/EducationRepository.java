package com.jb2dev.cv.domain.education.ports;

import com.jb2dev.cv.domain.education.model.EducationItem;

import java.util.List;
import java.util.Optional;

public interface EducationRepository {
  List<EducationItem> findAllEducations();
  Optional<EducationItem> findEducationById(int id);
}
