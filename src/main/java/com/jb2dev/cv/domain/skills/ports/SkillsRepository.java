package com.jb2dev.cv.domain.skills.ports;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.List;
import java.util.Optional;

public interface SkillsRepository {

  List<LanguageSkill> findAllLanguages(Language language);
  Optional<LanguageSkill> findLanguageById(int id, Language language);

  List<TechnicalSkill> findAllTechnicalSkills();
  List<TechnicalSkill> findTechnicalSkillByCategory(String category);
  List<TechnicalSkill> findTechnicalSkillByName(String name);

  List<SoftSkill> findAllSoftSkills(Language language);
  Optional<SoftSkill> findSoftSkillById(int id, Language language);
}
