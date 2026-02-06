package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.application.education.ListEducationsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListEducationsInteractor implements ListEducationsUseCase {

  private final EducationRepository educationRepository;

  @Override
  public List<EducationItem> execute(Language language) {
    log.info("Executing ListEducations use case for language: {}", language);
    List<EducationItem> result = educationRepository.findAllEducations(language);
    log.debug("Retrieved {} education items", result.size());
    return result;
  }
}
