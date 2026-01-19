package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetTechnicalSkillByIdService implements GetTechnicalSkillByIdUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public Optional<TechnicalSkill> execute(int id) {
    return queryPort.technicalById(id);
  }
}
