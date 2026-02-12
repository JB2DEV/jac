package com.jb2dev.cv.infrastructure.rest.mappers.skills;

import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.infrastructure.rest.dto.skills.LanguageSkillDetailResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.LanguageSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.SoftSkillResponse;
import com.jb2dev.cv.infrastructure.rest.dto.skills.TechnicalSkillDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SkillsRestMapper {
    LanguageSkillResponse toResponse(LanguageSkill skill);
    LanguageSkillDetailResponse toDetailResponse(LanguageSkill skill);
    List<LanguageSkillResponse> toLanguageResponseList(List<LanguageSkill> skills);

    TechnicalSkillDetailResponse toTechnicalDetailResponse(TechnicalSkill skill);

    List<TechnicalSkillDetailResponse> toTechnicalDetailResponseList(List<TechnicalSkill> skills);


    SoftSkillResponse toResponse(SoftSkill skill);

    List<SoftSkillResponse> toSoftResponseList(List<SoftSkill> skills);
}
