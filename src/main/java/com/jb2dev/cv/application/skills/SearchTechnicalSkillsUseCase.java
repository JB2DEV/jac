package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.application.skills.query.TechnicalSkillSearchCriteria;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.List;

@FunctionalInterface
public interface SearchTechnicalSkillsUseCase {
    List<TechnicalSkill> execute(TechnicalSkillSearchCriteria criteria);
}
