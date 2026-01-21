package com.jb2dev.cv.application.certifications.impl;

import com.jb2dev.cv.application.certifications.ListCertificationsUseCase;
import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.domain.certifications.ports.CertificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListCertificationsInteractor implements ListCertificationsUseCase {

  private final CertificationRepository certificationRepository;

  @Override
  public List<CertificationItem> execute() {
    return certificationRepository.findAllCertifications();
  }
}
