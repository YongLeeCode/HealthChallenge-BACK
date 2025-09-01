package com.healthmate.healthmate.domain.weeklylog.controller;

import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.UpsertCurrentWeekRequest;
import com.healthmate.healthmate.domain.weeklylog.dto.WeeklyLogDtos.WeeklyLogResponse;
import com.healthmate.healthmate.domain.weeklylog.service.WeeklyLogService;
import com.healthmate.healthmate.global.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weekly-logs")
@RequiredArgsConstructor
public class WeeklyLogController {

    private final WeeklyLogService weeklyLogService;

    @PutMapping("/current")
    public ResponseEntity<WeeklyLogResponse> upsertCurrent(@RequestBody UpsertCurrentWeekRequest req) {
        Long userId = SecurityUtils.getCurrentUserIdOrThrow();
        WeeklyLogResponse res = weeklyLogService.upsertCurrentWeek(userId, req);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<WeeklyLogResponse>> getMine() {
        Long userId = SecurityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(weeklyLogService.getMyLogs(userId));
    }
}


