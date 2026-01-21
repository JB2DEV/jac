package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.SearchTechnicalSkillsUseCase;
import com.jb2dev.cv.application.skills.query.TechnicalSkillSearchCriteria;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchTechnicalSkillsInteractor implements SearchTechnicalSkillsUseCase {

    private final SkillsRepository skillsRepository;

    @Override
    public List<TechnicalSkill> execute(TechnicalSkillSearchCriteria criteria) {

        if (criteria.name() != null) {
            return skillsRepository.findTechnicalSkillByName(criteria.name());
        }

        if (criteria.category() != null) {
            return skillsRepository.findTechnicalSkillByCategory(criteria.category());
        }

        return skillsRepository.findAllTechnicalSkills();
    }
}
