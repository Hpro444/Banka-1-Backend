package com.banka1.userService.advice;

import com.banka1.userService.dto.responses.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-dupe")
        public void throwDataIntegrity() {
            throw new DataIntegrityViolationException("dup key");
        }

        @GetMapping("/test-missing")
        public void throwNoSuchElement() {
            throw new NoSuchElementException("not found");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void dataIntegrityViolationReturns409() throws Exception {
        mockMvc.perform(get("/test-dupe"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ERR_CONSTRAINT_VIOLATION"));
    }

    @Test
    void noSuchElementReturns404() throws Exception {
        mockMvc.perform(get("/test-missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ERR_NOT_FOUND"));
    }
}
