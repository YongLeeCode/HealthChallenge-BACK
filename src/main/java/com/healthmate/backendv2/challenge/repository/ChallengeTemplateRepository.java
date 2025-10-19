package com.healthmate.backendv2.challenge.repository;

import com.healthmate.backendv2.challenge.entity.ChallengeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeTemplateRepository extends JpaRepository<ChallengeTemplate, Long> {

    /**
     * 현재 활성화된 챌린지 템플릿 조회 (현재 날짜가 시작일과 종료일 사이)
     */
	@Query("SELECT ct FROM ChallengeTemplate ct WHERE :currentDate BETWEEN ct.startDate AND ct.endDate")
    Optional<ChallengeTemplate> findActiveTemplateByDate(@Param("currentDate") LocalDate currentDate);

    // /**
    //  * 특정 날짜에 시작하는 챌린지 템플릿 조회
    //  */
    // @Query("SELECT ct FROM ChallengeTemplate ct WHERE ct.startDate = :startDate AND ct.isActive = true")
    // Optional<ChallengeTemplate> findByStartDate(@Param("startDate") LocalDate startDate);
	//
    // /**
    //  * 특정 기간과 겹치는 챌린지 템플릿 조회
    //  */
    // @Query("SELECT ct FROM ChallengeTemplate ct WHERE ct.isActive = true " +
    //        "AND ((ct.startDate <= :endDate AND ct.endDate >= :startDate))")
    // List<ChallengeTemplate> findOverlappingTemplates(@Param("startDate") LocalDate startDate,
    //                                                 @Param("endDate") LocalDate endDate);
	//
    // /**
    //  * 이름으로 챌린지 템플릿 조회
    //  */
    // Optional<ChallengeTemplate> findByName(String name);

    /**
     * 이름 중복 확인
     */
    boolean existsByName(String name);

    /**
     * 운동 ID가 포함된 챌린지 템플릿 조회
     */
    @Query("SELECT DISTINCT ct FROM ChallengeTemplate ct JOIN ct.exercises cte WHERE cte.exerciseId = :exerciseId")
    List<ChallengeTemplate> findByExerciseId(@Param("exerciseId") Long exerciseId);
}
