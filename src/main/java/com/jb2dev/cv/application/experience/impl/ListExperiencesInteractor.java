package com.jb2dev.cv.application.experience.impl;

import com.jb2dev.cv.application.experience.ListExperiencesUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListExperiencesInteractor implements ListExperiencesUseCase {

  private final ExperienceRepository experienceRepository;

  @Override
  public List<ExperienceItem> execute(Language language) {
    return experienceRepository.findAllExperiences(language);
  }
}
