package com.jb2dev.cv.infrastructure.rest.controllers.training;

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
class TrainingControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllTrainingsInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/training")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].credential_id", notNullValue()));
    }

    @Test
    void shouldGetAllTrainingsInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/training")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetAllTrainingsWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/training")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetTrainingByCredentialIdInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/training/4Aes")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.credential_id", is("4Aes")))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.provider", notNullValue()))
                .andExpect(jsonPath("$.location", notNullValue()))
                .andExpect(jsonPath("$.issued_date", notNullValue()))
                .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    void shouldGetTrainingByCredentialIdInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/training/4Aes")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.credential_id", is("4Aes")))
                .andExpect(jsonPath("$.title", notNullValue()));
    }

    @Test
    void shouldReturn404WhenTrainingNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/training/NON-EXISTENT-CERT")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateTrainingIssuedDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/training/4Aes")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issued_date", matchesRegex("\\d{4}-\\d{2}")));
    }

    @Test
    void shouldIncludeCredentialUrlWhenAvailable() throws Exception {
        mockMvc.perform(get("/api/v1/training/4Aes")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credential_url").exists());
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        mockMvc.perform(get("/api/v1/training")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));
    }

}
