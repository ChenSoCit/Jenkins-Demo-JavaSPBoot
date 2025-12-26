package com.java.TrainningJV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.TrainningJV.dtos.request.SignRequest;
import com.java.TrainningJV.exception.GlobalExceptionHandler;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add exception handler
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        SignRequest signRequest = new SignRequest();
        signRequest.setEmail("test@example.com");
        signRequest.setPassword("password123");

        User mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(authenticationService.login(any(SignRequest.class))).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/v3/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        SignRequest signRequest = new SignRequest();
        signRequest.setEmail("test@example.com");
        signRequest.setPassword("wrongpassword");

        when(authenticationService.login(any(SignRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        // Act & Assert
        // GlobalExceptionHandler will catch the exception and return proper ApiResponse
        mockMvc.perform(post("/api/v3/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
