package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import com.jb2dev.cv.infrastructure.rest.dto.skills.TechnicalSkillDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTechnicalSkillsService implements GetTechnicalSkillsUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public List<TechnicalSkill> execute() {
    return queryPort.technicalList();
  }
}
