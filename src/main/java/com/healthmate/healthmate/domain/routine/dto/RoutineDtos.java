package com.healthmate.healthmate.domain.routine.dto;

public class RoutineDtos {

    public record CreateRoutineRequest(String name) {}

    public record UpdateRoutineRequest(String name) {}

    public record RoutineResponse(Long id, String name, Long userId) {}
}
