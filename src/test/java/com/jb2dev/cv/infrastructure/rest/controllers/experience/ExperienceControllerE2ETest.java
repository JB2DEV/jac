package com.jb2dev.cv.infrastructure.rest.controllers.experience;

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
class ExperienceControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllExperiencesInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/experience")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].role", notNullValue()))
                .andExpect(jsonPath("$[0].summary", notNullValue()));
    }

    @Test
    void shouldGetAllExperiencesInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/experience")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetAllExperiencesWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/experience")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetExperienceByIdInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/experience/6541")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetExperienceByIdInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/experience/6541")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(6541)))
                .andExpect(jsonPath("$.role", notNullValue()));
    }

    @Test
    void shouldReturn404WhenExperienceNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/experience/999999")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateExperienceDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/experience/6541")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start_date", matchesRegex("\\d{4}-\\d{2}-\\d{2}")));
    }

    @Test
    void shouldHandleCurrentExperience() throws Exception {
        mockMvc.perform(get("/api/v1/experience")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        mockMvc.perform(get("/api/v1/experience")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));
    }
}
