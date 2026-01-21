package com.jb2dev.cv.domain.skills.ports;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.List;
import java.util.Optional;

public interface SkillsRepository {

  List<LanguageSkill> findAllLanguages();
  Optional<LanguageSkill> findLanguageById(int id);

  List<TechnicalSkill> findAllTechnicalSkills();
  List<TechnicalSkill> findTechnicalSkillByCategory(String category);
  List<TechnicalSkill> findTechnicalSkillByName(String name);

  List<SoftSkill> findAllSoftSkills();
  Optional<SoftSkill> findSoftSkillById(int id);
}
