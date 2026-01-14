package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListSoftSkillsService implements ListSoftSkillsUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public List<SoftSkill> execute() {
    return queryPort.soft();
  }
}
