package com.jb2dev.cv.application.certifications;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;
import com.jb2dev.cv.domain.certifications.ports.CertificationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GetCertificationByIdService implements GetCertificationByIdUseCase {

  private final CertificationQueryPort queryPort;

  @Override
  public Optional<CertificationItem> execute(int id) {
    return queryPort.findById(id);
  }
}
