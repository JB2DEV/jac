package com.jb2dev.cv.infrastructure.json.experience;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import com.jb2dev.cv.infrastructure.json.ClasspathJsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsonExperienceQueryAdapter implements ExperienceRepository {

    private static final TypeReference<List<ExperienceItem>> TYPE = new TypeReference<>() {};
    private final ClasspathJsonReader reader;

    @Override
    public List<ExperienceItem> findAllExperiences(Language language) {
        String path = language == Language.EN_EN ? "data/en/experience.json" : "data/es/experience.json";
        log.debug("Reading experience items from JSON file: {}", path);
        var items = reader.read(path, TYPE);
        List<ExperienceItem> sorted = items.stream()
                .sorted(Comparator.comparing(
                        ExperienceItem::startDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed())
                .toList();
        log.debug("Loaded {} experience items (sorted by date)", sorted.size());
        return sorted;
    }

    @Override
    public Optional<ExperienceItem> findExperienceById(int id, Language language) {
        log.debug("Searching experience by id: {}", id);
        return findAllExperiences(language).stream()
                .filter(i -> i.id() == id)
                .findFirst();
    }
}
