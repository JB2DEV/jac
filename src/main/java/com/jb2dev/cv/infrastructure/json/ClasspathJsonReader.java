package com.jb2dev.cv.infrastructure.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClasspathJsonReader {

  private final ObjectMapper objectMapper;

  public <T> T read(String classpathLocation, Class<T> clazz) {
    try {
      log.trace("Attempting to read JSON resource: {} as {}", classpathLocation, clazz.getSimpleName());
      ClassPathResource resource = new ClassPathResource(classpathLocation);
      try (InputStream in = resource.getInputStream()) {
        T result = objectMapper.readValue(in, clazz);
        log.trace("Successfully read JSON resource: {}", classpathLocation);
        return result;
      }
    } catch (Exception e) {
      log.error("Failed to read JSON resource: {}", classpathLocation, e);
      throw new IllegalStateException("Failed to read JSON resource: " + classpathLocation, e);
    }
  }

  public <T> T read(String classpathLocation, TypeReference<T> type) {
    try {
      log.trace("Attempting to read JSON resource: {} with TypeReference", classpathLocation);
      ClassPathResource resource = new ClassPathResource(classpathLocation);
      try (InputStream in = resource.getInputStream()) {
        T result = objectMapper.readValue(in, type);
        log.trace("Successfully read JSON resource: {}", classpathLocation);
        return result;
      }
    } catch (Exception e) {
      log.error("Failed to read JSON resource: {}", classpathLocation, e);
      throw new IllegalStateException("Failed to read JSON resource: " + classpathLocation, e);
    }
  }
}
