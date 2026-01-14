package com.jb2dev.cv.domain.certifications.ports;

import com.jb2dev.cv.domain.certifications.model.CertificationItem;

import java.util.List;
import java.util.Optional;

public interface CertificationQueryPort {
  List<CertificationItem> list();
  Optional<CertificationItem> findById(int id);
}
