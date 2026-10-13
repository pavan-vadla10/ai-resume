package com.airesume.checker.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.airesume.checker.dto.*;

@Service
public class SkillMatchingService {

    private final SkillExtractionService skillExtractionService;

    public SkillMatchingService(
            SkillExtractionService skillExtractionService) {
        this.skillExtractionService = skillExtractionService;
    }

    public SkillMatchResponse calculateMatch(
            String resumeText,
            String jobDescription) {

        Set<String> resumeSkills =
                skillExtractionService.extractSkills(resumeText);

        Set<String> requiredSkills =
                skillExtractionService.extractSkills(jobDescription);

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : requiredSkills) {
            if (resumeSkills.contains(skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        double score = requiredSkills.isEmpty()
                ? 0.0
                : Math.round(
                    matchedSkills.size() * 10000.0
                    / requiredSkills.size()
                ) / 100.0;

        return new SkillMatchResponse(
                new ArrayList<>(resumeSkills),
                new ArrayList<>(requiredSkills),
                matchedSkills,
                missingSkills,
                score
        );
    }
}