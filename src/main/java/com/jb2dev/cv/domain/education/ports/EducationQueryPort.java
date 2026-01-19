package com.jb2dev.cv.domain.education.ports;

import com.jb2dev.cv.domain.education.model.EducationItem;

import java.util.List;
import java.util.Optional;

public interface EducationQueryPort {
  List<EducationItem> list();
  Optional<EducationItem> findById(int id);
}
