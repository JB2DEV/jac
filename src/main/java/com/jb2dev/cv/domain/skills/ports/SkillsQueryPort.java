package com.jb2dev.cv.domain.skills.ports;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;

import java.util.List;
import java.util.Optional;

public interface SkillsQueryPort {

  List<LanguageSkill> languages();
  Optional<LanguageSkill> languageById(int id);

  List<TechnicalSkill> technical();
  Optional<TechnicalSkill> technicalById(int id);

  List<SoftSkill> soft();
  Optional<SoftSkill> softById(int id);
}
