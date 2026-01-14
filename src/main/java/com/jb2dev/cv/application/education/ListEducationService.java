package com.jb2dev.cv.application.education;

import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListEducationService implements ListEducationUseCase {

  private final EducationQueryPort queryPort;

  @Override
  public List<EducationItem> execute() {
    return queryPort.list();
  }
}
