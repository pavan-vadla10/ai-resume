package com.airesume.checker.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.airesume.checker.dto.AnalysisResponse;
import com.airesume.checker.dto.CreateAnalysisRequest;
import com.airesume.checker.dto.SkillMatchResponse;
import com.airesume.checker.entity.AnalysisStatus;
import com.airesume.checker.entity.ResumeAnalysis;
import com.airesume.checker.extractor.ResumeTextExtractor;
import com.airesume.checker.filestorage.FileStorageService;
import com.airesume.checker.repository.ResumeAnalysisRepository;

import jakarta.transaction.Transactional;

@Service
public class AnalysisService {

	private final ResumeAnalysisRepository resumeAnlysisRepo;
	private final FileStorageService fileStorageService;
	private final ResumeTextExtractor resumeTextExtractor;
	private final SkillMatchingService skillMatchingService;

	public AnalysisService(ResumeAnalysisRepository resumeAnlysisRepo, FileStorageService fileStorageService,
			ResumeTextExtractor resumeTextExtractor,SkillMatchingService skillMatchingService) {
		this.resumeAnlysisRepo = resumeAnlysisRepo;
		this.fileStorageService = fileStorageService;
		this.resumeTextExtractor = resumeTextExtractor;
		this.skillMatchingService = skillMatchingService;
	}

	public AnalysisResponse createFileUpload(MultipartFile filePath, String jobDescription) throws IOException {
		if (jobDescription == null || jobDescription.isBlank() || jobDescription.length() > 2000) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job Description must be 1 tp 2000 words");
		}
		if (filePath == null || filePath.isEmpty() || filePath.getSize() > 5L * 1024 * 1024) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Resume must be a non-empty file no larger than 5 MB");
		}

		String storageKey = null;

		try {
			storageKey = fileStorageService.store(filePath);

			Path storedPath = fileStorageService.resolve(storageKey);

			validateFileSignature(storedPath, storageKey);

			String extractedText = resumeTextExtractor.extract(storedPath);

			ResumeAnalysis analysis = new ResumeAnalysis();
			analysis.setOriginalFileName(safeDisplayName(filePath.getOriginalFilename()));
			analysis.setStoredKey(storageKey);
			analysis.setExtractedText(extractedText);
			analysis.setJobDescription(jobDescription);
			analysis.setStatus(AnalysisStatus.QUEUED);

			return saveFileAnalysis(analysis);

		} catch (IllegalArgumentException ex) {
			cleanup(storageKey);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);

		} catch (IOException ex) {
			cleanup(storageKey);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read the uploaded resume", ex);

		} catch (RuntimeException ex) {
			cleanup(storageKey);
			throw ex;
		}
	}

	private void cleanup(String storageKey) {
		if (storageKey == null) {
			return;
		}

		try {
			fileStorageService.delete(storageKey);
		} catch (IOException ignored) {
			// Production code should log the failure and
			// schedule orphan-file cleanup.
		}
	}

	private void validateFileSignature(Path path, String storageKey) throws IOException {

		byte[] header = new byte[4];

		try (var input = java.nio.file.Files.newInputStream(path)) {
			int bytesRead = input.read(header);

			if (bytesRead < 4) {
				throw new IllegalArgumentException("The uploaded file is empty or invalid");
			}
		}

		if (storageKey.endsWith(".pdf")) {
			boolean isPdf = header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F';

			if (!isPdf) {
				throw new IllegalArgumentException("The file does not have a valid PDF signature");
			}
		} else if (storageKey.endsWith(".docx")) {
			boolean isZip = header[0] == 'P' && header[1] == 'K' && header[2] == 3 && header[3] == 4;

			if (!isZip) {
				throw new IllegalArgumentException("The file does not have a valid DOCX container signature");
			}
		}
	}

	private String safeDisplayName(String originalName) {
		String name = java.nio.file.Paths.get(originalName.replace('\\', '/')).getFileName().toString();

		if (name.length() > 255) {
			name = name.substring(name.length() - 255);
		}

		return name;
	}

	@Transactional
	public AnalysisResponse saveFileAnalysis(ResumeAnalysis fileDetails) {

		ResumeAnalysis save = resumeAnlysisRepo.save(fileDetails);
		return toResponse(save);
	}

	@SuppressWarnings("deprecation")
	public AnalysisResponse getFiledAnalysis(UUID id) {
		ResumeAnalysis analysis = resumeAnlysisRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));

		return toResponse(analysis);
	}

	private AnalysisResponse toResponse(ResumeAnalysis analysis) {
		return new AnalysisResponse(analysis.getResumeId(), analysis.getOriginalFileName(), analysis.getStatus(),
				analysis.getOverallScore(), analysis.getCreatedAt(), analysis.getCompletedAt());
	}

	public SkillMatchResponse calculateSkillMatch(UUID id) {

		ResumeAnalysis analysis = resumeAnlysisRepo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analysis not found"));

		return skillMatchingService.calculateMatch(analysis.getExtractedText(), analysis.getJobDescription());
	}
}
