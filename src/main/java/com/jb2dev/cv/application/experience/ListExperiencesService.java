package com.jb2dev.cv.application.experience;

import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListExperiencesService implements ListExperiencesUseCase {

  private final ExperienceQueryPort queryPort;

  @Override
  public List<ExperienceItem> execute() {
    return queryPort.list();
  }
}
