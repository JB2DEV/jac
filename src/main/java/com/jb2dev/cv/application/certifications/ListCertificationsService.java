package com.jb2dev.cv.application.certifications;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.domain.certifications.ports.CertificationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ListCertificationsService implements ListCertificationsUseCase {

  private final CertificationQueryPort queryPort;

  @Override
  public List<CertificationItem> execute() {
    return queryPort.list();
  }
}
