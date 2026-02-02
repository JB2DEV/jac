package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.application.education.ListEducationsUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListEducationsInteractor implements ListEducationsUseCase {

  private final EducationRepository educationRepository;

  @Override
  public List<EducationItem> execute(Language language) {
    return educationRepository.findAllEducations(language);
  }
}
