package com.jb2dev.cv.infrastructure.rest.controllers.profile;

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
class ProfileControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetPersonalInfoInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/personal")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.full_name", notNullValue()))
                .andExpect(jsonPath("$.birth_date", notNullValue()))
                .andExpect(jsonPath("$.nationality", notNullValue()))
                .andExpect(jsonPath("$.gender", notNullValue()));
    }

    @Test
    void shouldGetPersonalInfoInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/personal")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.full_name", notNullValue()))
                .andExpect(jsonPath("$.birth_date", notNullValue()))
                .andExpect(jsonPath("$.nationality", notNullValue()))
                .andExpect(jsonPath("$.gender", notNullValue()));
    }

    @Test
    void shouldGetPersonalInfoWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/personal")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.full_name", notNullValue()));
    }

    @Test
    void shouldGetContactInfoInEnglish() throws Exception {
        mockMvc.perform(get("/api/v1/contact")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.mobile_phone", notNullValue()))
                .andExpect(jsonPath("$.landline_phone", notNullValue()))
                .andExpect(jsonPath("$.linkedin_url", notNullValue()))
                .andExpect(jsonPath("$.github_url", notNullValue()));
    }

    @Test
    void shouldGetContactInfoInSpanish() throws Exception {
        mockMvc.perform(get("/api/v1/contact")
                        .header("Accept-Language", "es_ES")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void shouldGetContactInfoWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/v1/contact")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void shouldValidateEmailFormat() throws Exception {
        mockMvc.perform(get("/api/v1/contact")
                        .header("Accept-Language", "en_EN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", matchesRegex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")));
    }

    @Test
    void shouldReturnJsonContentType() throws Exception {
        mockMvc.perform(get("/api/v1/personal")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("application/json")));
    }
}
