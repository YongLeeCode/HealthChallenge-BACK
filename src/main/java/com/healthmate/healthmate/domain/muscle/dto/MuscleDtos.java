package com.healthmate.healthmate.domain.muscle.dto;

public class MuscleDtos {

	public record 	CreateMuscleRequest(
		String korName,
		String engName,
		String category
	) {}

	public record UpdateMuscleRequest(
		String korName,
		String engName,
		String category
	) {}

	public record MuscleResponse(
		Long id,
		String korName,
		String engName,
		String category
	) {}
}


