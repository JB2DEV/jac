package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTechnicalSkillByCategoryService implements GetTechnicalSkillByCategoryUseCase {

  private final SkillsQueryPort queryPort;

    @Override
    public List<TechnicalSkill> execute(String category) {
        return queryPort.technicalByCategory(category);
    }
}
