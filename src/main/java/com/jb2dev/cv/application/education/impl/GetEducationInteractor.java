package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.application.education.GetEducationUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetEducationInteractor implements GetEducationUseCase {

  private final EducationRepository educationRepository;

  @Override
  public Optional<EducationItem> execute(int id, Language language) {
    return educationRepository.findEducationById(id, language);
  }
}
