package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetSoftSkillByIdService implements GetSoftSkillByIdUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public Optional<SoftSkill> execute(int id) {
    return queryPort.softById(id);
  }
}
