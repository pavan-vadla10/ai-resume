package com.airesume.checker.filestorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private final Path uploadDirectory;

	public FileStorageService(@Value("${app.upload.dir:./ats-uploads}") String uploadDirectory) {
		this.uploadDirectory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
	}

	public String store(MultipartFile file) throws IOException {
		String originalName = file.getOriginalFilename();
		if (file.isEmpty() || originalName == null || originalName.isBlank()) {
			throw new IllegalArgumentException("Please upload Correct File");
		}

		String lowerName = originalName.toLowerCase(Locale.ROOT);
		String extension;

		if (lowerName.endsWith(".pdf")) {
			extension = ".pdf";
		} else if (lowerName.endsWith(".docx")) {
			extension = ".docx";
		} else {
			throw new IllegalArgumentException("Only PDF and DOCX are supported");
		}

		// create directory i
		Files.createDirectories(uploadDirectory);

		// create StoregaeKey;

		String storageKey = UUID.randomUUID() + extension;

		Path destination = uploadDirectory.resolve(storageKey).normalize();

		if (!destination.startsWith(uploadDirectory)) {
			throw new IllegalArgumentException("Invalid file path");
		}

		Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

		return storageKey;

	}

	public Path resolve(String storageKey) {
		Path path = uploadDirectory.resolve(storageKey).normalize();

		if (!path.startsWith(uploadDirectory)) {
			throw new IllegalArgumentException("Invalid storage key");
		}

		return path;
	}

	public void delete(String storageKey) throws IOException {
		Files.deleteIfExists(resolve(storageKey));
	}
}
