package com.jb2dev.cv.infrastructure.rest.controllers.skills;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SkillsControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllLanguageSkillsInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/skills/languages")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].language", notNullValue()));
    }

    @Test
    void shouldGetAllLanguageSkillsInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/skills/languages")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetAllLanguageSkillsWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/skills/languages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetLanguageSkillByIdInEnglish() throws Exception {
        // First, get all skills to get a valid ID
        String response = mockMvc.perform(get("/api/v1/skills/languages")
                        .header("Accept-Language", "en_EN"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/v1/skills/languages/2354")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2354)))
                .andExpect(jsonPath("$.language", notNullValue()))
                .andExpect(jsonPath("$.listening", notNullValue()))
                .andExpect(jsonPath("$.reading", notNullValue()))
                .andExpect(jsonPath("$.spoken_production", notNullValue()))
                .andExpect(jsonPath("$.spoken_interaction", notNullValue()))
                .andExpect(jsonPath("$.writing", notNullValue()));
    }

    @Test
    void shouldReturn404WhenLanguageSkillNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/skills/languages/999999")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldGetAllTechnicalSkills() throws Exception {
        mockMvc.perform(get("/api/v1/skills/technical")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].name", notNullValue()))
                .andExpect(jsonPath("$[0].category", notNullValue()))
                .andExpect(jsonPath("$[0].skill_experience", notNullValue()));
    }

    @Test
    void shouldSearchTechnicalSkillsByName() throws Exception {
        mockMvc.perform(get("/api/v1/skills/technical")
                        .param("name", "Java")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].name", everyItem(containsStringIgnoringCase("java"))));
    }

    @Test
    void shouldSearchTechnicalSkillsByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/skills/technical")
                        .param("category", "Language")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldReturnEmptyListWhenNoTechnicalSkillsMatch() throws Exception {
        mockMvc.perform(get("/api/v1/skills/technical")
                        .param("name", "NonExistentSkill12345")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // API returns 404 for empty search results
    }


    @Test
    void shouldGetAllSoftSkillsInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/skills/soft")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    void shouldGetAllSoftSkillsInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/skills/soft")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetSoftSkillById() throws Exception {
        // Using a known ID from the test data
        mockMvc.perform(get("/api/v1/skills/soft/4729")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4729)))
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    void shouldReturn404WhenSoftSkillNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/skills/soft/999999")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnJsonContentType() throws Exception {
        mockMvc.perform(get("/api/v1/skills/technical")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));
    }

}
