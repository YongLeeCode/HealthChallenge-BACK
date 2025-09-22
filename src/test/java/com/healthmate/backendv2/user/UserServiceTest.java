package com.healthmate.backendv2.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.healthmate.backendv2.user.dto.UserDtos.Response;
import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;
import com.healthmate.backendv2.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    @DisplayName("유저 ID로 조회 테스트")
    public void testFindUserById() {
        // given
        Long userId = 1L;
        User mockUser = new User(userId, "testUser", "test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        
        // when
        Response foundUser = userService.getById(userId);
        
        // then
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
    }
    
    @Test
    @DisplayName("존재하지 않는 유저 조회시 예외 발생 테스트")
    public void testFindNonExistingUser() {
        // given
        Long nonExistingId = 999L;
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(RuntimeException.class, () -> {
            userService.getById(nonExistingId);
        });
    }
}
