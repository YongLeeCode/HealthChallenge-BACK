package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeSubmissionResponse;
import com.healthmate.backendv2.challenge.dto.WeeklyChallengeResponse;
import com.healthmate.backendv2.challenge.dto.WorkingTimeSubmissionRequest;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

/**
 * 챌린지 서비스의 공통 인터페이스
 * 각 챌린지 타입별로 구체적인 구현체가 필요
 */
public interface ChallengeService {
	Integer calculatePoints(Object a);
}
