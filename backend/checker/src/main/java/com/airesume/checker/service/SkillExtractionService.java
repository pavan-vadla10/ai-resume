package com.airesume.checker.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class SkillExtractionService {

	private static final Map<String, List<String>> SKILLS = new LinkedHashMap<>();

	static {
		SKILLS.put("Java", List.of("java"));
		SKILLS.put("Spring Boot", List.of("spring boot", "springboot"));
		SKILLS.put("Spring Security", List.of("spring security"));
		SKILLS.put("Spring Data JPA", List.of("spring data jpa", "jpa"));
		SKILLS.put("Angular", List.of("angular"));
		SKILLS.put("TypeScript", List.of("typescript"));
		SKILLS.put("JavaScript", List.of("javascript"));
		SKILLS.put("Python", List.of("python"));
		SKILLS.put("SQL", List.of("sql"));
		SKILLS.put("PostgreSQL", List.of("postgresql", "postgres"));
		SKILLS.put("MySQL", List.of("mysql"));
		SKILLS.put("REST API", List.of("rest api", "restful api", "restful services"));
		SKILLS.put("Microservices", List.of("microservices", "microservice"));
		SKILLS.put("Docker", List.of("docker"));
		SKILLS.put("Kubernetes", List.of("kubernetes", "k8s"));
		SKILLS.put("Kafka", List.of("kafka", "apache kafka"));
		SKILLS.put("Redis", List.of("redis"));
		SKILLS.put("Git", List.of("git", "github"));
		SKILLS.put("Jenkins", List.of("jenkins"));
		SKILLS.put("AWS", List.of("aws", "amazon web services"));
	}

	public Set<String> extractSkills(String text) {
		Set<String> found = new LinkedHashSet<>();

		if (text == null || text.isBlank()) {
			return found;
		}

		String normalized = text.toLowerCase(Locale.ROOT);

		SKILLS.forEach((skill, aliases) -> {
			for (String alias : aliases) {
				String regex = "(?<![a-z0-9+#.])" + Pattern.quote(alias) + "(?![a-z0-9+#.])";

				if (Pattern.compile(regex).matcher(normalized).find()) {
					found.add(skill);
					break;
				}
			}
		});

		return found;
	}
}
