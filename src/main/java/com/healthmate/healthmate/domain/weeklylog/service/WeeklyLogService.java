package com.healthmate.healthmate.domain.weeklylog.service;

import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.UpsertCurrentWeekRequest;
import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.WeeklyLogResponse;

import java.util.List;

public interface WeeklyLogService {
    WeeklyLogResponse upsertCurrentWeek(Long userId, UpsertCurrentWeekRequest req);
    List<WeeklyLogResponse> getMyLogs(Long userId);
}


