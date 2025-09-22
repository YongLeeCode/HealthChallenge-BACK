package com.healthmate.backendv2.user.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.healthmate.backendv2.user.dto.UserDtos.*;
import com.healthmate.backendv2.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public Response getById(Long id) {
		return Response.from(userRepository.findById(id).orElseThrow());
	}
}


