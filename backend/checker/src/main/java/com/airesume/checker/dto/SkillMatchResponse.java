package com.airesume.checker.dto;

import java.util.List;

public record SkillMatchResponse(List<String> resumeSkills, List<String> requiredSkills, List<String> matchedSkills,
		List<String> missingSkills, double matchScore) {
}