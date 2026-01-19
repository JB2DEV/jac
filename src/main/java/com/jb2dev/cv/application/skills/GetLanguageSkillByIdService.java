package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetLanguageSkillByIdService implements GetLanguageSkillByIdUseCase {

  private final SkillsQueryPort queryPort;

  @Override
  public Optional<LanguageSkill> execute(int id) {
    return queryPort.languageById(id);
  }
}
