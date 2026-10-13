package com.airesume.checker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.airesume.checker.entity.AnalysisStatus;

public record AnalysisResponse(
	    UUID id,
	    String originalFilename,
	    AnalysisStatus status,
	    Integer overallScore,
	    LocalDateTime createdAt,
	    LocalDateTime completedAt
	) {
	}