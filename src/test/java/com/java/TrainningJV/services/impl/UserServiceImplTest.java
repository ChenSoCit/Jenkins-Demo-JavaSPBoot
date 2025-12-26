package com.java.TrainningJV.services.impl;

import com.java.TrainningJV.dtos.request.UserRequest;
import com.java.TrainningJV.dtos.response.RoleCountResponse;
import com.java.TrainningJV.mappers.UserMapper;
import com.java.TrainningJV.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .address("123 Main St")
                .gender("Male")
                .dateOfBirth(Date.valueOf("1990-01-01"))
                .phoneNumber("1234567890")
                .roleId(1L)
                .build();

        userRequest = new UserRequest();
        userRequest.setFirstName("Jane");
        userRequest.setLastName("Smith");
        userRequest.setEmail("jane.smith@example.com");
        userRequest.setPassword("password456");
        userRequest.setAddress("456 Oak St");
        userRequest.setGender("Female");
        userRequest.setDateOfBirth("1992-02-02");
        userRequest.setPhoneNumber("0987654321");
        userRequest.setRoleId(2L);
    }

    @Test
    void testGetUser_Success() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(mockUser);

        // Act
        User result = userService.getUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getFirstName(), result.getFirstName());
        verify(userMapper, times(1)).getUserById(1L);
    }

    @Test
    void testGetUser_NotFound() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(null);

        // Act
        User result = userService.getUser(999L);

        // Assert
        assertNull(result);
        verify(userMapper, times(1)).getUserById(999L);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userMapper.createUser(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // Act
        long userId = userService.createUser(userRequest);

        // Assert
        assertEquals(1L, userId);
        verify(userMapper, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUser_Failure() {
        // Arrange
        when(userMapper.createUser(any(User.class))).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(userRequest);
        });

        assertEquals("Failed to create user", exception.getMessage());
        verify(userMapper, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(mockUser);
        when(userMapper.updateUser(any(User.class))).thenReturn(1);

        // Act
        int result = userService.updateUser(1L, userRequest);

        // Assert
        assertEquals(1, result);
        verify(userMapper, times(1)).getUserById(1L);
        verify(userMapper, times(1)).updateUser(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, userRequest);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userMapper, times(1)).getUserById(999L);
        verify(userMapper, never()).updateUser(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(mockUser);
        when(userMapper.deleteUser(anyLong())).thenReturn(1);

        // Act
        int result = userService.deleteUser(1L);

        // Assert
        assertEquals(1, result);
        verify(userMapper, times(1)).getUserById(1L);
        verify(userMapper, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userMapper, times(1)).getUserById(999L);
        verify(userMapper, never()).deleteUser(anyLong());
    }

    @Test
    void testDeleteUser_DeleteFailed() {
        // Arrange
        when(userMapper.getUserById(anyLong())).thenReturn(mockUser);
        when(userMapper.deleteUser(anyLong())).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("Failed to delete user with id: 1", exception.getMessage());
        verify(userMapper, times(1)).getUserById(1L);
        verify(userMapper, times(1)).deleteUser(1L);
    }

    @Test
    void testGetUserNoneRole_Success() {
        // Arrange
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).firstName("User1").build(),
                User.builder().id(2L).firstName("User2").build()
        );
        when(userMapper.getUserNoneRole()).thenReturn(mockUsers);

        // Act
        List<User> result = userService.getUserNoneRole();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userMapper, times(1)).getUserNoneRole();
    }

    @Test
    void testGetUserRole_Success() {
        // Arrange
        Long roleId = 1L;
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).firstName("Admin1").roleId(roleId).build(),
                User.builder().id(2L).firstName("Admin2").roleId(roleId).build()
        );
        when(userMapper.getUserRole(anyLong())).thenReturn(mockUsers);

        // Act
        List<User> result = userService.getUserRole(roleId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(roleId, result.get(0).getRoleId());
        verify(userMapper, times(1)).getUserRole(roleId);
    }

    @Test
    void testGetRoleCount_Success() {
        // Arrange
        List<RoleCountResponse> mockRoleCounts = Arrays.asList(
                new RoleCountResponse(1L, "Admin", 5L),
                new RoleCountResponse(2L, "User", 10L)
        );
        when(userMapper.countUserRole()).thenReturn(mockRoleCounts);

        // Act
        List<RoleCountResponse> result = userService.getRoleCount();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Admin", result.get(0).getRoleName());
        assertEquals(5L, result.get(0).getUserCount());
        verify(userMapper, times(1)).countUserRole();
    }

    @Test
    void testGetAllUsers_ReturnsEmptyList() {
        // Act
        List<User> result = userService.getAllUsers(0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
