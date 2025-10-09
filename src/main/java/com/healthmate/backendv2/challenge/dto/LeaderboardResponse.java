package com.healthmate.backendv2.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponse {
    
    private String challengePeriod;
    private Integer currentUserRank;
    private Integer currentUserPoints;
    private List<LeaderboardEntry> entries;
    private Integer totalParticipants;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaderboardEntry {
        private Integer rank;
        private String nickname;
        private Integer points;
        private String profileImageUrl;
    }
}
