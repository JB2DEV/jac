package com.jb2dev.cv.application.experience.impl;

import com.jb2dev.cv.application.experience.GetExperienceUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetExperienceInteractor implements GetExperienceUseCase {

  private final ExperienceRepository experienceRepository;

  @Override
  public ExperienceItem execute(int id, Language language) {
    return experienceRepository.findExperienceById(id, language)
        .orElseThrow(() -> new ResourceNotFoundException("Experience", String.valueOf(id)));
  }
}
