package com.java.TrainningJV.services.impl;

import com.java.TrainningJV.dtos.request.SignRequest;
import com.java.TrainningJV.mappers.UserMapper;
import com.java.TrainningJV.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private SignRequest signRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        signRequest = new SignRequest();
        signRequest.setEmail("test@example.com");
        signRequest.setPassword("password123");

        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(userMapper.findByEmail(anyString())).thenReturn(mockUser);

        // Act
        User result = authenticationService.login(signRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getEmail(), result.getEmail());
        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userMapper.findByEmail(anyString())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.login(signRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogin_WrongPassword() {
        // Arrange
        User userWithDifferentPassword = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("differentPassword")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userMapper.findByEmail(anyString())).thenReturn(userWithDifferentPassword);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.login(signRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userMapper, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogin_NullPassword() {
        // Arrange
        signRequest.setPassword(null);
        when(userMapper.findByEmail(anyString())).thenReturn(mockUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.login(signRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
