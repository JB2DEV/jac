package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.application.education.GetEducationUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetEducationInteractor implements GetEducationUseCase {

  private final EducationRepository educationRepository;

  @Override
  public EducationItem execute(int id, Language language) {
    log.info("Executing GetEducation use case: id={}, language={}", id, language);

    EducationItem result = educationRepository.findEducationById(id, language)
        .orElseThrow(() -> {
          log.warn("Education not found: id={}, language={}", id, language);
          return new ResourceNotFoundException("Education", String.valueOf(id));
        });

    log.debug("Education retrieved: id={}, title={}, institution={}, location={}, startDate={}, endDate={}",
        result.id(), result.title(), result.institution(), result.location(), result.startDate(), result.endDate());
    return result;
  }
}
