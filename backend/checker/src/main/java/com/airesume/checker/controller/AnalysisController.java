package com.airesume.checker.controller;


import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.airesume.checker.dto.AnalysisResponse;
import com.airesume.checker.dto.CreateAnalysisRequest;
import com.airesume.checker.dto.SkillMatchResponse;
import com.airesume.checker.service.AnalysisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value="/file-analysis")
public class AnalysisController {
	private final AnalysisService analysisService;
	public AnalysisController(AnalysisService analysisService) {
		this.analysisService = analysisService;
	}
	
	@PostMapping(value="/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<AnalysisResponse> create(
	        @RequestPart("resume") MultipartFile resume,
	        @RequestPart("jobDescription") String jobDescription) {

	    AnalysisResponse response = null;
		try {
			response = analysisService.createFileUpload(resume, jobDescription);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(response);
	}

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponse> getById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
            analysisService.getFiledAnalysis(id)
        );
    }
    
    @GetMapping("/{id}/skill-match")
    public ResponseEntity<SkillMatchResponse> getSkillMatch(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                analysisService.calculateSkillMatch(id)
        );
    }
}
