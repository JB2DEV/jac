package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.application.education.GetEducationUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetEducationInteractor implements GetEducationUseCase {

  private final EducationRepository educationRepository;

  @Override
  public EducationItem execute(int id, Language language) {
    return educationRepository.findEducationById(id, language)
        .orElseThrow(() -> new ResourceNotFoundException("Education", String.valueOf(id)));
  }
}
