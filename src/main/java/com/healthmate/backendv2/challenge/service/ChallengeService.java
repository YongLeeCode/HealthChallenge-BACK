package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.exercise.MeasurementType;

/**
 * 챌린지 서비스의 공통 인터페이스
 * 각 챌린지 타입별로 구체적인 구현체가 필요
 */
public interface ChallengeService {

	/**
	 * 운동 제출 데이터를 기반으로 점수 계산
	 * @param exerciseSubmission 운동 제출 데이터
	 * @return 계산된 점수
	 */
	Integer calculatePoints(
		com.healthmate.backendv2.challenge.dto.ChallengeBatchSubmissionRequest.ExerciseSubmission exerciseSubmission);

	/**
	 * 이 서비스가 처리할 수 있는 측정 타입 반환
	 * @return MeasurementType
	 */
	MeasurementType getSupportedMeasurementType();
}
