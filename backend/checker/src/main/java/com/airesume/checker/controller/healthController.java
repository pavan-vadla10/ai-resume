package com.airesume.checker.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthController {
	
	@GetMapping("/getHealth")
	public Map<String,String> getHealth(){
		return Map.of("Application","ATS CHecker","Status","UP");
	}

}
