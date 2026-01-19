package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListLanguageSkillsService implements ListLanguageSkillsUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public List<LanguageSkill> execute() {
    return queryPort.languages();
  }
}
