package com.airesume.checker.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysis {
	
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID resumeId;

	@Column(nullable = false, length = 255)
	private String originalFileName;

	@Column(nullable = false,columnDefinition = "TEXT")
	private String jobDescription;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AnalysisStatus status;

	private Integer overallScore;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime completedAt;
	
	@Column(nullable=false)
	private String storedKey;
	
	@Column(nullable=false,columnDefinition = "TEXT")
	private String extractedText;

	public ResumeAnalysis() {
	}

	public String getStoredKey() {
		return storedKey;
	}


	public void setStoredKey(String storedKey) {
		this.storedKey = storedKey;
	}


	public String getExtractedText() {
		return extractedText;
	}


	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}


	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (status == null) {
			status = AnalysisStatus.QUEUED;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		if (status == AnalysisStatus.COMPLETED || status == AnalysisStatus.FAILED) {
			completedAt = LocalDateTime.now();
		}
	}

	public UUID getResumeId() {
		return resumeId;
	}

	public void setResumeId(UUID resumeId) {
		this.resumeId = resumeId;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public AnalysisStatus getStatus() {
		return status;
	}

	public void setStatus(AnalysisStatus status) {
		this.status = status;
	}

	public Integer getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

}
