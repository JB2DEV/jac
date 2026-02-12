package com.jb2dev.cv.infrastructure.rest.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnErrorResponseForInvalidEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/invalid-endpoint"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotBreakSwaggerUI() throws Exception {
        mockMvc.perform(get("/api/v1/swagger-ui"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldNotBreakOpenApiDocs() throws Exception {
        mockMvc.perform(get("/api/v1/openapi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void shouldServeActualEndpointsNormally() throws Exception {
        mockMvc.perform(get("/api/v1/personal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
