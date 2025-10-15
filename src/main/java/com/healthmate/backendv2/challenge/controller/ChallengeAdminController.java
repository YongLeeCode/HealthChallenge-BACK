package com.healthmate.backendv2.challenge.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthmate.backendv2.auth.config.JwtUtils;
import com.healthmate.backendv2.challenge.dto.ChallengeTemplateCreateRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeTemplateResponse;
import com.healthmate.backendv2.challenge.service.ChallengeBatchService;
import com.healthmate.backendv2.challenge.service.ChallengeService;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithTimeAttack;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWeight;
import com.healthmate.backendv2.challenge.service.ChallengeServiceWithWorkingTime;
import com.healthmate.backendv2.challenge.service.ChallengeTemplateService;
import com.healthmate.backendv2.challenge.service.LeaderboardService;

@RestController
@RequestMapping("/api/challenges/admin")
@RequiredArgsConstructor
@Slf4j
public class ChallengeAdminController {

	private final LeaderboardService leaderboardService;
	private final ChallengeBatchService challengeBatchService;
	private final ChallengeTemplateService challengeTemplateService;
	private final JwtUtils jwtUtils;

	/**
	 * 챌린지 템플릿 생성 (운영진용)
	 */
	@PostMapping("/templates")
	public ResponseEntity<ChallengeTemplateResponse> createChallengeTemplate(
		@Valid @RequestBody ChallengeTemplateCreateRequest request) {

		log.info("Creating challenge template: {}", request.getName());
		ChallengeTemplateResponse response = challengeTemplateService.createChallengeTemplate(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 모든 챌린지 템플릿 조회 (운영진용)
	 */
	@GetMapping("/templates")
	public ResponseEntity<List<ChallengeTemplateResponse>> getAllChallengeTemplates() {
		List<ChallengeTemplateResponse> response = challengeTemplateService.getAllChallengeTemplates();
		return ResponseEntity.ok(response);
	}

	/**
	 * 활성화된 챌린지 템플릿 조회 (운영진용)
	 */
	@GetMapping("/templates/active")
	public ResponseEntity<List<ChallengeTemplateResponse>> getActiveChallengeTemplates() {
		List<ChallengeTemplateResponse> response = challengeTemplateService.getActiveChallengeTemplates();
		return ResponseEntity.ok(response);
	}

	/**
	 * 현재 활성화된 챌린지 템플릿 조회 (운영진용)
	 */
	@GetMapping("/templates/current")
	public ResponseEntity<ChallengeTemplateResponse> getCurrentActiveTemplate() {
		return challengeTemplateService.getCurrentActiveTemplate()
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * 특정 챌린지 템플릿 조회 (운영진용)
	 */
	@GetMapping("/admin/templates/{id}")
	public ResponseEntity<ChallengeTemplateResponse> getChallengeTemplateById(@PathVariable Long id) {
		return challengeTemplateService.getChallengeTemplateById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * 챌린지 템플릿 비활성화 (운영진용)
	 */
	@DeleteMapping("/admin/templates/{id}")
	public ResponseEntity<String> deactivateChallengeTemplate(@PathVariable Long id) {
		challengeTemplateService.deactivateChallengeTemplate(id);
		return ResponseEntity.ok("챌린지 템플릿이 비활성화되었습니다.");
	}

	/**
	 * 현재 활성화된 챌린지의 운동 목록 조회 (사용자용)
	 */
	@GetMapping("/current/exercises")
	public ResponseEntity<List<Long>> getCurrentActiveExerciseIds() {
		List<Long> exerciseIds = challengeTemplateService.getCurrentActiveExerciseIds();
		return ResponseEntity.ok(exerciseIds);
	}

	/**
	 * JWT에서 사용자 ID 추출
	 */
	private Long getCurrentUserIdFromJWT(HttpServletRequest request) {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization header가 없거나 Bearer 토큰이 아닙니다.");
		}

		String jwt = authHeader.substring(7);

		try {
			// JWT에서 userId 추출
			Long userId = jwtUtils.extractUserId(jwt);
			log.info("Extracted userId from JWT: {}", userId);
			return userId;
		} catch (Exception e) {
			log.error("JWT에서 userId 추출 실패: {}", e.getMessage());
			throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
		}
	}

	/**
	 * 인증된 사용자 ID 추출 (Authentication 사용)
	 */
	private Long getCurrentUserId(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			throw new IllegalArgumentException("인증된 사용자가 아닙니다.");
		}

		// CustomUserPrincipal에서 사용자 ID 추출
		if (authentication.getPrincipal() instanceof com.healthmate.backendv2.auth.config.CustomUserPrincipal) {
			com.healthmate.backendv2.auth.config.CustomUserPrincipal userPrincipal =
				(com.healthmate.backendv2.auth.config.CustomUserPrincipal) authentication.getPrincipal();

			Long userId = userPrincipal.getId();
			log.info("Extracted userId from authentication: {}", userId);
			return userId;
		}

		throw new IllegalArgumentException("유효하지 않은 인증 정보입니다.");
	}
}
