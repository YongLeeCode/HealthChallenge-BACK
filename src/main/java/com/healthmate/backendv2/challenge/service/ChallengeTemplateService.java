package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeTemplateCreateRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeTemplateResponse;
import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import com.healthmate.backendv2.challenge.repository.ChallengeTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeTemplateService {

    private final ChallengeTemplateRepository challengeTemplateRepository;

    /**
     * 챌린지 템플릿 생성
     */
    @Transactional
    public ChallengeTemplateResponse createChallengeTemplate(ChallengeTemplateCreateRequest request) {
        log.info("Creating challenge template: {}", request.getName());

        // 이름 중복 확인
        if (challengeTemplateRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 챌린지 이름입니다: " + request.getName());
        }

        // 날짜 유효성 검증
        validateDateRange(request.getStartDate(), request.getEndDate());

        // 챌린지 템플릿 생성
        ChallengeTemplate template = ChallengeTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdAt(LocalDate.now())
                .build();

        ChallengeTemplate savedTemplate = challengeTemplateRepository.save(template);
        log.info("Challenge template created successfully: {}", savedTemplate.getId());

        // 요청에 유닛 ID가 있으면 저장
        if (request.getExerciseUnitIds() != null && !request.getExerciseUnitIds().isEmpty()) {
            request.getExerciseUnitIds().forEach(savedTemplate::addExerciseUnitId);
            challengeTemplateRepository.save(savedTemplate);
        }

        return ChallengeTemplateResponse.from(savedTemplate);
    }

    /**
     * 현재 활성화된 챌린지 템플릿의 운동 ID 목록 조회
     */
    public List<Long> getCurrentActiveExerciseIds() {
        return challengeTemplateRepository.findActiveTemplateByDate(LocalDate.now())
                .map(ChallengeTemplate::getExerciseUnitIds)
                .orElse(List.of());
    }

    /**
     * 현재 활성화된 챌린지 템플릿 조회
     */
    public Optional<ChallengeTemplateResponse> getCurrentActiveTemplate() {
        return challengeTemplateRepository.findActiveTemplateByDate(LocalDate.now())
                .map(ChallengeTemplateResponse::from);
    }

    // 기존 운동 템플릿 생성 로직은 제거되었습니다. 이제 템플릿은 유닛 ID 목록만 참조합니다.

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("시작일은 오늘 이후여야 합니다");
        }
    }

}
