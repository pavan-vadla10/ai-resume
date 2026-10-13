package com.airesume.checker.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

@Service
public class ResumeTextExtractor {

	public String extract(Path filePath) throws IOException {
		String fileName = filePath.getFileName().toString().toLowerCase(Locale.ROOT);

		String text;
		if (fileName.endsWith(".pdf")) {
			text = extractPdf(filePath);
		} else if (fileName.endsWith(".docx")) {
			text = extractDocx(filePath);
		} else {
			throw new IllegalArgumentException("Invalid File Uploaded!!!");
		}
		
		  text = text.replaceAll("[\\t\\x0B\\f\\r]+", " ")
                  .replaceAll(" +", " ")
                  .trim();

       if (text.isBlank()) {
           throw new IllegalArgumentException(
                   "No readable text found. The PDF may be scanned or empty.");
       }

       return text;
	}
	
	private String extractDocx(Path filePath) throws IOException{
		try(InputStream input = Files.newInputStream(filePath)){
			XWPFDocument document = new XWPFDocument(input);
			XWPFWordExtractor wordText = new XWPFWordExtractor(document);
			return wordText.getText();
		}
	}
	
	 private String extractPdf(Path path) throws IOException {
	        try (PDDocument document = Loader.loadPDF(path.toFile())) {
	            PDFTextStripper stripper = new PDFTextStripper();
	            return stripper.getText(document);
	        }
	    }

}
