package com.jb2dev.cv.application.certifications;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;

import java.util.List;

@FunctionalInterface
public interface ListCertificationsUseCase {
  List<CertificationItem> execute();
}
