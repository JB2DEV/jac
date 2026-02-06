package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.application.profile.GetContactInfoUseCase;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GetContactInfoInteractor implements GetContactInfoUseCase {

  private final ContactInfoRepository contactInfoRepository;

  @Override
  public ContactInfo execute(Language language) {
    log.info("Executing GetContactInfo use case for language: {}", language);
    ContactInfo result = contactInfoRepository.getContactInfo(language);
    log.debug("Contact info retrieved: email={}, phone={}, address={}, linkedIn={}, github={}",
        result.email(), result.mobilePhone(), result.address(), result.linkedinUrl(), result.githubUrl());
    return result;
  }
}
