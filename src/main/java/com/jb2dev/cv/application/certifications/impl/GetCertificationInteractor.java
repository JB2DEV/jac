package com.jb2dev.cv.application.certifications.impl;

import com.jb2dev.cv.application.certifications.GetCertificationUseCase;
import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.domain.certifications.ports.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetCertificationInteractor implements GetCertificationUseCase {

  private final CertificationRepository certificationRepository;

  @Override
  public Optional<CertificationItem> execute(int id) {
    return certificationRepository.findCertificationById(id);
  }
}
