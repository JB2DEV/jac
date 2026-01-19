package com.jb2dev.cv.domain.training.ports;

import com.jb2dev.cv.domain.training.model.TrainingItem;

import java.util.List;
import java.util.Optional;

public interface TrainingQueryPort {
  List<TrainingItem> list();
  Optional<TrainingItem> findById(String credentialId);
}
