package com.nvd.training.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@GetMapping("/products")
    public String getMessage() {
        return "Say 'Hello' from Product brand";
    }
}
