package com.jb2dev.cv.application.experience.impl;

import com.jb2dev.cv.application.experience.ListExperiencesUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListExperiencesInteractor implements ListExperiencesUseCase {

  private final ExperienceRepository experienceRepository;

  @Override
  public List<ExperienceItem> execute(Language language) {
    log.info("Executing ListExperiences use case for language: {}", language);
    List<ExperienceItem> result = experienceRepository.findAllExperiences(language);
    log.debug("Retrieved {} experience items", result.size());
    return result;
  }
}
