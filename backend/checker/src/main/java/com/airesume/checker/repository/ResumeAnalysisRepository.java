package com.airesume.checker.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.airesume.checker.entity.ResumeAnalysis;


public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis,UUID>{

}
