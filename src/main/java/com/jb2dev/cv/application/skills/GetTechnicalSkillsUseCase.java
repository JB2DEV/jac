package com.jb2dev.cv.application.skills;

import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.infrastructure.rest.dto.skills.TechnicalSkillDetailResponse;

import java.util.List;

@FunctionalInterface
public interface GetTechnicalSkillsUseCase {
  List<TechnicalSkill> execute();
}
