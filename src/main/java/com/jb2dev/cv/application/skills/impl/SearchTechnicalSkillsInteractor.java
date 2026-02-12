package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.SearchTechnicalSkillsUseCase;
import com.jb2dev.cv.application.skills.query.TechnicalSkillSearchCriteria;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchTechnicalSkillsInteractor implements SearchTechnicalSkillsUseCase {

    private final SkillsRepository skillsRepository;

    @Override
    public List<TechnicalSkill> execute(TechnicalSkillSearchCriteria criteria) {
        log.info("Executing SearchTechnicalSkills use case with criteria: name={}, category={}",
                criteria.name(), criteria.category());

        List<TechnicalSkill> results;
        if (criteria.name() != null) {
            results = skillsRepository.findTechnicalSkillByName(criteria.name());
        } else if (criteria.category() != null) {
            results = skillsRepository.findTechnicalSkillByCategory(criteria.category());
        } else {
            results = skillsRepository.findAllTechnicalSkills();
        }

        log.debug("Found {} technical skills matching criteria", results.size());
        return results;
    }
}
