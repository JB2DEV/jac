package com.jb2dev.cv.application.certifications;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;

import java.util.Optional;

@FunctionalInterface
public interface GetCertificationByIdUseCase {
  Optional<CertificationItem> execute(int id);
}
