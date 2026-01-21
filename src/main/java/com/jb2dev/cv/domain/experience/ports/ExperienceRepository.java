package com.jb2dev.cv.domain.experience.ports;

import com.jb2dev.cv.domain.experience.model.ExperienceItem;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository {
  List<ExperienceItem> findAllExperiences();
  Optional<ExperienceItem> findExperienceById(int id);
}
