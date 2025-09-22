package com.healthmate.backendv2.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.healthmate.backendv2.user.dto.UserDtos.Response;
import com.healthmate.backendv2.user.dto.UserDtos.ProfileUpdateRequest;
import com.healthmate.backendv2.user.dto.UserDtos.PasswordChangeRequest;
import com.healthmate.backendv2.user.entity.User;
import com.healthmate.backendv2.user.repository.UserRepository;
import com.healthmate.backendv2.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;

	@Test
	@DisplayName("유저 프로필 변경 테스트")
	public void testUpdateProfile() {
		//given
		Long userId = 1L;
		User existingUser = mockUser();
		ProfileUpdateRequest request = ProfileUpdateRequest.builder()
				.username("updatedUser")
				.email("updated@example.com")
				.profileImageUrl("https://example.com/profile.jpg")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		Response result = userService.updateProfile(userId, request);

		// then
		assertNotNull(result);
		assertEquals("updatedUser", result.getUsername());
		assertEquals("updated@example.com", result.getEmail());
		assertEquals("https://example.com/profile.jpg", result.getProfileImageUrl());
		assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday());
		verify(userRepository).save(any(User.class));
	}

	@Test
	@DisplayName("유저 비밀번호 변경 테스트")
	public void testChangePassword() {
		//given
		Long userId = 1L;
		User existingUser = mockUserWithPassword("encodedPassword");
		PasswordChangeRequest request = PasswordChangeRequest.builder()
				.currentPassword("oldPassword")
				.newPassword("newPassword")
				.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		userService.changePassword(userId, request);

		// then
		verify(passwordEncoder).matches("oldPassword", "encodedPassword");
		verify(passwordEncoder).encode("newPassword");
		verify(userRepository).save(any(User.class));
	}

	@Test
	@DisplayName("잘못된 현재 비밀번호로 비밀번호 변경시 예외 발생 테스트")
	public void testChangePasswordWithWrongCurrentPassword() {
		//given
		Long userId = 1L;
		User existingUser = mockUserWithPassword("encodedPassword");
		PasswordChangeRequest request = PasswordChangeRequest.builder()
				.currentPassword("wrongPassword")
				.newPassword("newPassword")
				.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

		// when & then
		assertThrows(IllegalArgumentException.class, () -> {
			userService.changePassword(userId, request);
		});
	}

    @Test
    @DisplayName("유저 ID로 조회 테스트")
    public void testFindUserById() {
        // given
        Long userId = 1L;
        User mockUser = mockUser();

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

	private User mockUser() {
		return User.builder()
				.id(1L)
				.username("testUser")
				.email("test@example.com")
				.password("encodedPassword")
				.rankTier(com.healthmate.backendv2.user.RankTier.BRONZE)
				.createdAt(java.time.OffsetDateTime.now())
				.updatedAt(java.time.OffsetDateTime.now())
				.build();
	}

	private User mockUserWithPassword(String password) {
		return User.builder()
				.id(1L)
				.username("testUser")
				.email("test@example.com")
				.password(password)
				.rankTier(com.healthmate.backendv2.user.RankTier.BRONZE)
				.createdAt(java.time.OffsetDateTime.now())
				.updatedAt(java.time.OffsetDateTime.now())
				.build();
	}
}
