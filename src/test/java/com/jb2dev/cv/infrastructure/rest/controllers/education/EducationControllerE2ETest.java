package com.jb2dev.cv.infrastructure.rest.controllers.education;

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
class EducationControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllEducationsInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/education")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].institution", notNullValue()))
                .andExpect(jsonPath("$[0].start_date", notNullValue()));
    }

    @Test
    void shouldGetAllEducationsInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/education")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetAllEducationsWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/education")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldGetEducationByIdInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/education/8792")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
