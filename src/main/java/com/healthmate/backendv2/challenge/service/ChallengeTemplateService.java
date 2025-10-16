package com.healthmate.backendv2.challenge.service;

import com.healthmate.backendv2.challenge.dto.ChallengeTemplateCreateRequest;
import com.healthmate.backendv2.challenge.dto.ChallengeTemplateResponse;
import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import com.healthmate.backendv2.challenge.entity.ChallengeTemplateExercise;
import com.healthmate.backendv2.challenge.entity.TimeAttackTemplateExercise;
import com.healthmate.backendv2.challenge.entity.WeightTemplateExercise;
import com.healthmate.backendv2.challenge.entity.WorkingTimeTemplateExercise;
import com.healthmate.backendv2.challenge.repository.ChallengeTemplateRepository;
import com.healthmate.backendv2.exercise.dto.ExerciseResponse;
import com.healthmate.backendv2.exercise.service.ExerciseService;
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
    private final ExerciseService exerciseService;

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

        // 운동 템플릿 추가
        for (int i = 0; i < request.getExercises().size(); i++) {
            ChallengeTemplateCreateRequest.ExerciseTemplateRequest exerciseRequest = request.getExercises().get(i);
            
            ChallengeTemplateExercise exercise = createExerciseTemplate(exerciseRequest, i);
            template.addExercise(exercise);
        }

        ChallengeTemplate savedTemplate = challengeTemplateRepository.save(template);
        log.info("Challenge template created successfully: {}", savedTemplate.getId());

        List<ExerciseResponse> exerciseResponses = savedTemplate.getExercises().stream()
                .map(exercise -> exerciseService.getById(exercise.getExerciseId()))
                .filter(exercise -> exercise != null)
                .toList();

        return ChallengeTemplateResponse.from(savedTemplate, exerciseResponses);
    }

    // /**
    //  * 모든 챌린지 템플릿 조회
    //  */
    // public List<ChallengeTemplateResponse> getAllChallengeTemplates() {
    //     return challengeTemplateRepository.findAll().stream()
    //             .map(template -> {
    //                 List<ExerciseResponse> exerciseResponses = template.getExercises().stream()
    //                         .map(exercise -> exerciseService.getById(exercise.getExerciseId()))
    //                         .filter(exercise -> exercise != null)
    //                         .toList();
    //                 return ChallengeTemplateResponse.from(template, exerciseResponses);
    //             })
    //             .toList();
    // }
	//
    // /**
    //  * 활성화된 챌린지 템플릿 조회
    //  */
    // public List<ChallengeTemplateResponse> getActiveChallengeTemplates() {
    //     return challengeTemplateRepository.findByIsActiveTrue().stream()
    //             .map(template -> {
    //                 List<ExerciseResponse> exerciseResponses = template.getExercises().stream()
    //                         .map(exercise -> exerciseService.getById(exercise.getExerciseId()))
    //                         .filter(exercise -> exercise != null)
    //                         .toList();
    //                 return ChallengeTemplateResponse.from(template, exerciseResponses);
    //             })
    //             .toList();
    // }
	//
    // /**
    //  * 현재 활성화된 챌린지 템플릿 조회
    //  */
    // public Optional<ChallengeTemplateResponse> getCurrentActiveTemplate() {
    //     return challengeTemplateRepository.findActiveTemplateByDate(LocalDate.now())
    //             .map(template -> {
    //                 List<ExerciseResponse> exerciseResponses = template.getExercises().stream()
    //                         .map(exercise -> exerciseService.getById(exercise.getExerciseId()))
    //                         .filter(exercise -> exercise != null)
    //                         .toList();
    //                 return ChallengeTemplateResponse.from(template, exerciseResponses);
    //             });
    // }
	//
    // /**
    //  * 특정 챌린지 템플릿 조회
    //  */
    // public Optional<ChallengeTemplateResponse> getChallengeTemplateById(Long id) {
    //     return challengeTemplateRepository.findById(id)
    //             .map(template -> {
    //                 List<ExerciseResponse> exerciseResponses = template.getExercises().stream()
    //                         .map(exercise -> exerciseService.getById(exercise.getExerciseId()))
    //                         .filter(exercise -> exercise != null)
    //                         .toList();
    //                 return ChallengeTemplateResponse.from(template, exerciseResponses);
    //             });
    // }
	//
    // /**
    //  * 챌린지 템플릿에 포함된 운동 ID 목록 조회
    //  */
    // public List<Long> getExerciseIdsByTemplateId(Long templateId) {
    //     return challengeTemplateRepository.findById(templateId)
    //             .map(template -> template.getExercises().stream()
    //                     .map(ChallengeTemplateExercise::getExerciseId)
    //                     .toList())
    //             .orElse(List.of());
    // }
	//
    /**
     * 현재 활성화된 챌린지 템플릿의 운동 ID 목록 조회
     */
    public List<Long> getCurrentActiveExerciseIds() {
        return challengeTemplateRepository.findActiveTemplateByDate(LocalDate.now())
                .map(template -> template.getExercises().stream()
                        .map(ChallengeTemplateExercise::getExerciseId)
                        .toList())
                .orElse(List.of());
    }
	//
    // /**
    //  * 운동 ID가 현재 활성화된 챌린지에 포함되는지 확인
    //  */
    // public boolean isExerciseAllowedInCurrentChallenge(Long exerciseId) {
    //     List<Long> allowedExerciseIds = getCurrentActiveExerciseIds();
    //     return allowedExerciseIds.contains(exerciseId);
    // }
	//
    // /**
    //  * 챌린지 템플릿 비활성화
    //  */
    // @Transactional
    // public void deactivateChallengeTemplate(Long id) {
    //     ChallengeTemplate template = challengeTemplateRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 챌린지 템플릿입니다: " + id));
	//
    //     template = template.toBuilder()
    //             .isActive(false)
    //             .build();
	//
    //     challengeTemplateRepository.save(template);
    //     log.info("Challenge template deactivated: {}", id);
    // }
	//
    // Private helper methods

    /**
     * 타입별 운동 템플릿 생성
     */
    private ChallengeTemplateExercise createExerciseTemplate(
            ChallengeTemplateCreateRequest.ExerciseTemplateRequest exerciseRequest, int index) {
        
        int orderIndex = exerciseRequest.getOrderIndex() != null ? exerciseRequest.getOrderIndex() : index;
        
        return switch (exerciseRequest.getType()) {
            case TIME_ATTACK -> {
                if (exerciseRequest instanceof ChallengeTemplateCreateRequest.TimeAttackExerciseTemplateRequest timeAttackRequest) {
                    yield TimeAttackTemplateExercise.builder()
                            .exerciseId(timeAttackRequest.getExerciseId())
                            .measurementType(timeAttackRequest.getType())
                            .orderIndex(orderIndex)
                            .pointsPerSecond(timeAttackRequest.getPointsPerSecond())
                            .maxPoints(timeAttackRequest.getMaxPoints())
                            .build();
                }
                throw new IllegalArgumentException("TIME_ATTACK 타입은 TimeAttackExerciseTemplateRequest여야 합니다");
            }
            case WEIGHT -> {
                if (exerciseRequest instanceof ChallengeTemplateCreateRequest.WeightExerciseTemplateRequest weightRequest) {
                    yield WeightTemplateExercise.builder()
                            .exerciseId(weightRequest.getExerciseId())
                            .measurementType(weightRequest.getType())
                            .orderIndex(orderIndex)
                            .pointsPerWeight(weightRequest.getPointsPerWeight())
                            .pointsPerCount(weightRequest.getPointsPerCount())
                            .build();
                }
                throw new IllegalArgumentException("WEIGHT 타입은 WeightExerciseTemplateRequest여야 합니다");
            }
            case WORKING_TIME -> {
                if (exerciseRequest instanceof ChallengeTemplateCreateRequest.WorkingTimeExerciseTemplateRequest workingTimeRequest) {
                    yield WorkingTimeTemplateExercise.builder()
                            .exerciseId(workingTimeRequest.getExerciseId())
                            .measurementType(workingTimeRequest.getType())
                            .orderIndex(orderIndex)
                            .pointsPerSecond(workingTimeRequest.getPointsPerSecond())
                            .build();
                }
                throw new IllegalArgumentException("WORKING_TIME 타입은 WorkingTimeExerciseTemplateRequest여야 합니다");
            }
            default -> throw new IllegalArgumentException("지원하지 않는 운동 타입입니다: " + exerciseRequest.getType());
        };
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("시작일은 오늘 이후여야 합니다");
        }
    }

    // private void validateExercisesExist(List<ChallengeTemplateCreateRequest.ExerciseTemplateRequest> exercises) {
    //     for (ChallengeTemplateCreateRequest.ExerciseTemplateRequest exercise : exercises) {
    //         if (exerciseService.getById(exercise.getExerciseId()) == null) {
    //             throw new IllegalArgumentException("존재하지 않는 운동입니다: " + exercise.getExerciseId());
    //         }
    //     }
    // }
	//
    // private ChallengeTemplate.ChallengeStatus determineStatus(LocalDate startDate, LocalDate endDate) {
    //     LocalDate now = LocalDate.now();
    //     if (now.isBefore(startDate)) {
    //         return ChallengeTemplate.ChallengeStatus.UPCOMING;
    //     } else if (now.isAfter(endDate)) {
    //         return ChallengeTemplate.ChallengeStatus.COMPLETED;
    //     } else {
    //         return ChallengeTemplate.ChallengeStatus.ACTIVE;
    //     }
    // }
}
