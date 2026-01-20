package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetTechnicalSkillByNameService implements GetTechnicalSkillByNameUseCase {

  private final SkillsQueryPort queryPort;

    @Override
    public List<TechnicalSkill> execute(String name) {
        return queryPort.technicalByName(name);
    }
}
