package com.java.TrainningJV.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.exception.GlobalExceptionHandler;
import com.java.TrainningJV.models.User;
import com.java.TrainningJV.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add exception handler
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        User mockUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        when(userService.getUser(userId)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user sc successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    void testGetUserNoneRole_Success() throws Exception {
        // Arrange
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).firstName("User1").build(),
                User.builder().id(2L).firstName("User2").build()
        );

        when(userService.getUserNoneRole()).thenReturn(mockUsers);

        // Act & Assert
        mockMvc.perform(get("/api/users/none-role")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user none role"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetUserByRole_Success() throws Exception {
        // Arrange
        Long roleId = 1L;
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).firstName("Admin1").roleId(roleId).build(),
                User.builder().id(2L).firstName("Admin2").roleId(roleId).build()
        );

        when(userService.getUserRole(roleId)).thenReturn(mockUsers);

        // Act & Assert
        mockMvc.perform(get("/api/users/roles/{roleId}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get user by role"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetCountRole_Success() throws Exception {
        // Arrange
        List<RoleCountResponse> mockRoleCounts = Arrays.asList(
                new RoleCountResponse(1L, "Admin", 5L),
                new RoleCountResponse(2L, "User", 10L)
        );

        when(userService.getRoleCount()).thenReturn(mockRoleCounts);

        // Act & Assert
        mockMvc.perform(get("/api/users/role-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("get count role"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Arrange
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setPassword("password123");
        userRequest.setAddress("123 Main St");
        userRequest.setGender("Male");
        userRequest.setDateOfBirth("1990-01-01");
        userRequest.setPhoneNumber("1234567890");
        userRequest.setRoleId(1L);

        when(userService.createUser(any(UserRequest.class))).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data").value(1));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("Jane");
        userRequest.setLastName("Doe");
        userRequest.setEmail("jane.doe@example.com");
        userRequest.setPassword("newpassword");
        userRequest.setAddress("456 Oak St");
        userRequest.setGender("Female");
        userRequest.setDateOfBirth("1992-02-02");
        userRequest.setPhoneNumber("0987654321");
        userRequest.setRoleId(2L);

        when(userService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("Jane");
        userRequest.setLastName("Doe");
        userRequest.setEmail("jane.doe@example.com");
        userRequest.setPassword("newpassword");
        userRequest.setAddress("456 Oak St");
        userRequest.setGender("Female");
        userRequest.setDateOfBirth("1992-02-02");
        userRequest.setPhoneNumber("0987654321");
        userRequest.setRoleId(2L);

        // Service throws RuntimeException when user not found
        when(userService.updateUser(eq(userId), any(UserRequest.class)))
                .thenThrow(new RuntimeException("User not found with id: " + userId));

        // Act & Assert
        // GlobalExceptionHandler will catch the exception and return proper ApiResponse
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(1);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        // Service throws RuntimeException when user not found
        when(userService.deleteUser(userId))
                .thenThrow(new RuntimeException("User not found with id: " + userId));

        // Act & Assert
        // GlobalExceptionHandler will catch the exception and return proper ApiResponse
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));
    }
}
