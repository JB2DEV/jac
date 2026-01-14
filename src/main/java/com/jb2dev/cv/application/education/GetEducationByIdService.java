package com.jb2dev.cv.application.education;

import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetEducationByIdService implements GetEducationByIdUseCase {

  private final EducationQueryPort queryPort;

  @Override
  public Optional<EducationItem> execute(int id) {
    return queryPort.findById(id);
  }
}
