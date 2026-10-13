package com.airesume.checker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAnalysisRequest(

	    @NotBlank(message = "Resume filename is required")
	    @Size(max = 255, message = "Filename is too long")
	    String originalFilename,

	    @NotBlank(message = "Job description is required")
	    @Size(max = 20000, message = "Job description is too long")
	    String jobDescription

	) {
	}
