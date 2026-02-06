package com.jb2dev.cv.application.experience.impl;

import com.jb2dev.cv.application.experience.GetExperienceUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetExperienceInteractor implements GetExperienceUseCase {

  private final ExperienceRepository experienceRepository;

  @Override
  public ExperienceItem execute(int id, Language language) {
    log.info("Executing GetExperience use case: id={}, language={}", id, language);

    ExperienceItem result = experienceRepository.findExperienceById(id, language)
        .orElseThrow(() -> {
          log.warn("Experience not found: id={}, language={}", id, language);
          return new ResourceNotFoundException("Experience", String.valueOf(id));
        });

    log.debug("Experience retrieved: id={}, role={}, company={}, location={}, startDate={}, endDate={}, current={}",
        result.id(), result.role(), result.company(), result.location(), result.startDate(), result.endDate(), result.current());
    return result;
  }
}
