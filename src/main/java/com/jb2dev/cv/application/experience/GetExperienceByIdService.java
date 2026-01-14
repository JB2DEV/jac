package com.jb2dev.cv.application.experience;

import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetExperienceByIdService implements GetExperienceByIdUseCase {

  private final ExperienceQueryPort queryPort;

  @Override
  public Optional<ExperienceItem> execute(int id) {
    return queryPort.findById(id);
  }
}
